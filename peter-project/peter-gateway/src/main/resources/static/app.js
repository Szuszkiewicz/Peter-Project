const MODULE_WEBSITE = 1;
const TYPE_ARTICLE = 1;

const state = {
  view: "articles",
  categories: [],
  articles: [],
  articlePage: 1,
  articlePageSize: 10,
  articleTotal: 0,
  selectedArticleId: null,
  comments: [],
  commentPage: 1,
  commentPageSize: 10,
  commentTotal: 0,
  users: [],
  userPage: 1,
  userPageSize: 10,
  userTotal: 0,
  settings: loadSettings(),
  contract: null,
  articleAll: [],
};

const viewMeta = {
  articles: ["文章", "浏览、筛选、编辑文章，并进入对应评论"],
  articleEditor: ["写文章", "新增或修改文章内容"],
  comments: ["评论", "按模块和资源 ID 管理评论"],
  users: ["用户", "登录、注册、查询、编辑和维护用户"],
  categories: ["分类", "维护文章分类"],
  settings: ["设置", "配置三个服务地址和当前请求用户"],
};

const $ = (selector) => document.querySelector(selector);

function defaultSettings() {
  return {
    articleBase: window.location.origin,
    commentBase: window.location.origin,
    userBase: window.location.origin,
    userId: 1,
    username: "Peter",
    avatar: "",
    token: "",
  };
}

function loadSettings() {
  const saved = localStorage.getItem("peterCmsSettings");
  if (!saved) return defaultSettings();
  try {
    const settings = { ...defaultSettings(), ...JSON.parse(saved) };
    if (window.location.origin === "http://localhost:8494") {
      if (settings.commentBase === "http://localhost:8488") settings.commentBase = window.location.origin;
      if (settings.userBase === "http://localhost:8489") settings.userBase = window.location.origin;
    }
    return settings;
  } catch {
    localStorage.removeItem("peterCmsSettings");
    return defaultSettings();
  }
}

function saveSettings(nextSettings) {
  state.settings = { ...state.settings, ...nextSettings };
  localStorage.setItem("peterCmsSettings", JSON.stringify(state.settings));
  renderApiStatus();
}

function renderApiStatus() {
  $("#apiStatus").textContent = `文章 ${state.settings.articleBase} | 评论 ${state.settings.commentBase} | 用户 ${state.settings.userBase}`;
}

function serviceBase(service) {
  return {
    article: state.settings.articleBase,
    comment: state.settings.commentBase,
    user: state.settings.userBase,
  }[service];
}

function buildUrl(service, path, params = {}) {
  const url = new URL(path, serviceBase(service));
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== "") {
      url.searchParams.set(key, value);
    }
  });
  return url.toString();
}

function requestHeaders(hasBody) {
  const headers = {
    userId: String(state.settings.userId || ""),
    username: encodeURIComponent(state.settings.username || ""),
    avatar: encodeURIComponent(state.settings.avatar || ""),
  };
  if (state.settings.token) {
    headers.token = state.settings.token;
  }
  if (hasBody) {
    headers["Content-Type"] = "application/json";
  }
  return headers;
}

async function api(service, path, options = {}) {
  const hasBody = options.body !== undefined;
  const response = await fetch(buildUrl(service, path, options.params), {
    method: options.method || "GET",
    headers: requestHeaders(hasBody),
    body: hasBody ? JSON.stringify(options.body) : undefined,
  });
  const text = await response.text();
  const payload = text ? JSON.parse(text) : null;
  if (!response.ok) throw new Error(`HTTP ${response.status}`);
  if (payload && payload.success === false) {
    throw new Error(payload.message || payload.msg || "请求失败");
  }
  return payload;
}

function showToast(message) {
  const toast = $("#toast");
  toast.textContent = message;
  toast.classList.add("show");
  clearTimeout(showToast.timer);
  showToast.timer = setTimeout(() => toast.classList.remove("show"), 2400);
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

function formatNumber(value) {
  return Number(value || 0).toLocaleString("zh-CN");
}

function totalPage(total, pageSize) {
  return Math.max(1, Math.ceil(Number(total || 0) / Number(pageSize || 10)));
}

function parseTags(tags) {
  if (!tags) return [];
  try {
    const parsed = JSON.parse(tags);
    if (Array.isArray(parsed)) return parsed.map(String).filter(Boolean);
  } catch {
    return String(tags)
      .split(/[，,]/)
      .map((item) => item.trim())
      .filter(Boolean);
  }
  return [];
}

function categoryName(id) {
  const item = state.categories.find((category) => String(category.id) === String(id));
  return item ? item.name : `分类 ${id || "-"}`;
}

function statusLabel(status) {
  return Number(status) === 2 ? "已发布" : "草稿";
}

function typeLabel(type) {
  return ({ 1: "文章", 2: "视频", 3: "公告" })[Number(type)] || "未知";
}

function userStatusLabel(status) {
  return ({ 1: "正常", 2: "冻结", 3: "注销" })[Number(status)] || "未知";
}

function roleLabel(role) {
  return Number(role) === 2 ? "管理员" : "普通用户";
}

function setView(view) {
  state.view = view;
  document.querySelectorAll(".nav-item").forEach((item) => {
    item.classList.toggle("active", item.dataset.view === view);
  });
  document.querySelectorAll(".view").forEach((item) => item.classList.remove("active"));
  $(`#${view}View`).classList.add("active");
  $("#viewTitle").textContent = viewMeta[view][0];
  $("#viewSubtitle").textContent = viewMeta[view][1];
  if (view === "categories") renderCategories();
  if (view === "comments") fillCommentDefaults();
  if (view === "users" && !state.users.length) loadUsers().catch((error) => showToast(error.message));
  if (view === "settings") fillSettingsForm();
}

async function loadCategories() {
  const result = await api("article", "/category/selectAll");
  state.categories = result?.data || [];
  renderCategoryOptions();
  renderCategories();
}

function renderCategoryOptions() {
  const options = state.categories
    .map((item) => `<option value="${escapeHtml(item.id)}">${escapeHtml(item.name)}</option>`)
    .join("");
  $("#articleCategoryFilter").innerHTML = `<option value="">全部分类</option>${options}`;
  $("#articleCategory").innerHTML = options || `<option value="">暂无分类</option>`;
}

async function loadArticles() {
  const result = await api("article", "/article/query/list", {
    method: "POST",
    body: {
      module: MODULE_WEBSITE,
      type: TYPE_ARTICLE,
      userId: Number(state.settings.userId),
      categoryId: $("#articleCategoryFilter").value ? Number($("#articleCategoryFilter").value) : undefined,
      status: $("#articleStatusFilter").value ? Number($("#articleStatusFilter").value) : undefined,
    },
  });
  state.articleAll = result?.data || [];
  state.articleTotal = state.articleAll.length;
  const start = (state.articlePage - 1) * state.articlePageSize;
  state.articles = state.articleAll.slice(start, start + state.articlePageSize);
  renderArticles();
}

function renderArticles() {
  const pageTotal = totalPage(state.articleTotal, state.articlePageSize);
  $("#articleTotal").textContent = `${state.articleTotal} 条`;
  $("#articlePageInfo").textContent = `${state.articlePage} / ${pageTotal}`;
  $("#articlePrev").disabled = state.articlePage <= 1;
  $("#articleNext").disabled = state.articlePage >= pageTotal;

  if (!state.articles.length) {
    $("#articleList").innerHTML = `<div class="empty">暂无文章</div>`;
    return;
  }

  $("#articleList").innerHTML = state.articles
    .map((article) => {
      const tags = parseTags(article.tags)
        .slice(0, 3)
        .map((tag) => `<span class="tag">${escapeHtml(tag)}</span>`)
        .join("");
      return `
        <article class="article-card ${String(article.id) === String(state.selectedArticleId) ? "active" : ""}">
          <div class="article-title">
            <strong>${escapeHtml(article.title || "未命名文章")}</strong>
            <span class="tag ${Number(article.status) === 1 ? "warning" : ""}">${statusLabel(article.status)}</span>
          </div>
          <div class="meta">
            <span>${escapeHtml(categoryName(article.categoryId))}</span>
            <span>${typeLabel(article.type)}</span>
            <span>浏览 ${formatNumber(article.views)}</span>
            <span>点赞 ${formatNumber(article.likes)}</span>
            <span>${escapeHtml(article.createTime || "")}</span>
          </div>
          ${article.desc ? `<div class="muted">${escapeHtml(article.desc)}</div>` : ""}
          ${tags ? `<div class="meta">${tags}</div>` : ""}
          <div class="card-actions">
            <button data-action="article-detail" data-id="${article.id}">详情</button>
            <button data-action="article-edit" data-id="${article.id}">编辑</button>
            <button data-action="article-comments" data-id="${article.id}">评论</button>
            <button data-action="article-like" data-id="${article.id}">点赞/取消</button>
            <button data-action="article-collect" data-id="${article.id}">收藏/取消</button>
            <button class="danger" data-action="article-delete" data-id="${article.id}">删除</button>
          </div>
        </article>
      `;
    })
    .join("");
}

async function loadArticleDetail(id) {
  const result = await api("article", "/article/query", { params: { id } });
  const article = result?.data;
  if (!article) throw new Error("未查询到文章详情");
  state.selectedArticleId = article.id;
  renderArticles();
  renderArticleDetail(article);
  loadRecommend(article.id).catch(() => {});
}

function renderArticleDetail(article) {
  const tags = parseTags(article.tags)
    .map((tag) => `<span class="tag">${escapeHtml(tag)}</span>`)
    .join("");
  $("#articleDetail").innerHTML = `
    ${article.cover ? `<img class="detail-cover" src="${escapeHtml(article.cover)}" alt="文章封面" />` : ""}
    <h2 class="detail-title">${escapeHtml(article.title || "未命名文章")}</h2>
    <div class="meta">
      <span>${escapeHtml(categoryName(article.categoryId))}</span>
      <span>${typeLabel(article.type)}</span>
      <span>${statusLabel(article.status)}</span>
      <span>浏览 ${formatNumber(article.views)}</span>
      <span>点赞 ${formatNumber(article.likes)}</span>
      <span>收藏 ${formatNumber(article.collects)}</span>
    </div>
    ${tags ? `<div class="meta" style="margin-top: 10px">${tags}</div>` : ""}
    ${article.desc ? `<p class="detail-desc">${escapeHtml(article.desc)}</p>` : ""}
    <div class="card-actions">
      <button data-action="article-edit" data-id="${article.id}">编辑</button>
      <button data-action="article-comments" data-id="${article.id}">查看评论</button>
    </div>
    <div class="detail-content">${escapeHtml(article.content || "")}</div>
    <div class="recommend" id="recommendBox">
      <h3>相关推荐</h3>
      <div class="muted">加载中...</div>
    </div>
  `;
}

async function loadRecommend(id) {
  const result = await api("article", `/article/selectRecommend/${id}`);
  const items = Array.isArray(result?.data) ? result.data : [];
  $("#recommendBox").innerHTML = `
    <h3>相关推荐</h3>
    ${
      items.length
        ? items.map((item) => `<button data-action="article-detail" data-id="${item.id}">${escapeHtml(item.title)}</button>`).join("")
        : `<div class="muted">暂无推荐</div>`
    }
  `;
}

function fillArticleForm(article = {}) {
  $("#articleId").value = article.id || "";
  $("#articleTitle").value = article.title || "";
  $("#articleCategory").value = article.categoryId || state.categories[0]?.id || "";
  $("#articleStatus").value = article.status || 2;
  $("#articleType").value = article.type || TYPE_ARTICLE;
  $("#articleCover").value = article.cover || "";
  $("#articleTags").value = parseTags(article.tags).join(", ");
  $("#articleDesc").value = article.desc || "";
  $("#articleContent").value = article.content || "";
}

async function editArticle(id) {
  const result = await api("article", "/article/query", { params: { id } });
  if (!result?.data) throw new Error("未查询到文章");
  fillArticleForm(result.data);
  setView("articleEditor");
}

async function saveArticle(event) {
  event.preventDefault();
  const body = {
    id: $("#articleId").value || undefined,
    module: MODULE_WEBSITE,
    type: Number($("#articleType").value),
    status: Number($("#articleStatus").value),
    title: $("#articleTitle").value.trim(),
    content: $("#articleContent").value.trim(),
    userId: Number(state.settings.userId),
    categoryId: Number($("#articleCategory").value),
    cover: $("#articleCover").value.trim(),
    tags: $("#articleTags").value.trim(),
    desc: $("#articleDesc").value.trim(),
  };
  if (!body.categoryId) throw new Error("请先创建或选择分类");
  await api("article", "/article/post", { method: "POST", body });
  showToast("文章已保存");
  fillArticleForm();
  state.articlePage = 1;
  await loadArticles();
  setView("articles");
}

async function deleteArticle(id) {
  if (!window.confirm("确认删除这篇文章？")) return;
  const result = await api("article", "/article/delete", {
    method: "POST",
    body: { id: Number(id), userId: Number(state.settings.userId) },
  });
  if (result?.data !== true) {
    throw new Error("删除失败，无法删除他人的文章");
  }
  state.articles = state.articles.filter((article) => String(article.id) !== String(id));
  state.articleTotal = Math.max(0, state.articleTotal - 1);
  if (String(state.selectedArticleId) === String(id)) {
    state.selectedArticleId = null;
    $("#articleDetail").innerHTML = `<div class="empty">选择一篇文章查看详情</div>`;
  }
  renderArticles();
  showToast("文章已删除");
  await loadArticles();
}

async function setArticleLike(id) {
  await api("article", "/like/set", {
    method: "POST",
    body: { targetId: Number(id), userId: Number(state.settings.userId), module: TYPE_ARTICLE },
  });
  showToast("文章点赞状态已更新");
  await loadArticles();
}

async function setArticleCollect(id) {
  await api("article", "/collect/set", {
    method: "POST",
    body: { targetId: Number(id), userId: Number(state.settings.userId), module: TYPE_ARTICLE },
  });
  showToast("文章收藏状态已更新");
  await loadArticles();
}

function openArticleComments(id) {
  $("#commentModule").value = MODULE_WEBSITE;
  $("#commentResourceId").value = id;
  state.commentPage = 1;
  setView("comments");
  loadComments().catch((error) => showToast(error.message));
}

function fillCommentDefaults() {
  if (!$("#commentModule").value) $("#commentModule").value = MODULE_WEBSITE;
  if (!$("#commentUserId").value) $("#commentUserId").value = state.settings.userId || "";
}

async function loadComments() {
  const result = await api("comment", "/comment/query", {
    params: {
      module: $("#commentModule").value,
      resourceId: $("#commentResourceId").value,
      score: $("#commentScoreFilter").value,
      order: $("#commentOrder").value,
      pageNum: state.commentPage,
      pageSize: state.commentPageSize,
    },
  });
  const data = result?.data || {};
  state.comments = data.list || [];
  state.commentTotal = Number(data.total || 0);
  renderComments();
}

function renderComments() {
  const pageTotal = totalPage(state.commentTotal, state.commentPageSize);
  $("#commentTotal").textContent = `${state.commentTotal} 条`;
  $("#commentPageInfo").textContent = `${state.commentPage} / ${pageTotal}`;
  $("#commentPrev").disabled = state.commentPage <= 1;
  $("#commentNext").disabled = state.commentPage >= pageTotal;

  if (!state.comments.length) {
    $("#commentList").innerHTML = `<div class="empty">暂无评论</div>`;
    return;
  }

  $("#commentList").innerHTML = state.comments
    .map(
      (comment) => `
        <article class="article-card">
          <div class="article-title">
            <strong>${escapeHtml(comment.userName || comment.username || `用户 ${comment.userId || "-"}`)}</strong>
            <span class="tag">${escapeHtml(comment.score || "-")} 分</span>
          </div>
          <div class="meta">
            <span>评论 ID ${escapeHtml(comment.commentId)}</span>
            <span>资源 ${escapeHtml(comment.resourceId)}</span>
            <span>点赞 ${formatNumber(comment.likeNum)}</span>
            <span>${escapeHtml(comment.commentTime || "")}</span>
          </div>
          <div class="detail-content">${escapeHtml(comment.content || "")}</div>
          <div class="card-actions">
            <button data-action="comment-like" data-id="${comment.commentId}">点赞/取消</button>
            <button class="danger" data-action="comment-delete" data-id="${comment.commentId}" data-resource="${comment.resourceId}" data-module="${comment.module}">删除</button>
          </div>
        </article>
      `
    )
    .join("");
}

async function saveComment(event) {
  event.preventDefault();
  await api("comment", "/comment/add", {
    method: "POST",
    body: {
      userId: $("#commentUserId").value.trim(),
      content: $("#commentContent").value.trim(),
      module: Number($("#commentModule").value),
      resourceId: $("#commentResourceId").value.trim(),
      score: Number($("#commentScore").value),
    },
  });
  showToast("评论已提交");
  $("#commentContent").value = "";
  state.commentPage = 1;
  await loadComments();
}

async function deleteComment(button) {
  if (!window.confirm("确认删除这条评论？")) return;
  await api("comment", "/comment/delete", {
    method: "DELETE",
    body: {
      commentId: String(button.dataset.id),
      userId: String(state.settings.userId),
      module: Number(button.dataset.module || $("#commentModule").value),
      resourceId: String(button.dataset.resource || $("#commentResourceId").value),
    },
  });
  showToast("评论已删除");
  await loadComments();
}

async function likeComment(id) {
  await api("comment", "/likes/set", {
    method: "POST",
    body: { commentId: Number(id), userId: Number(state.settings.userId) },
  });
  showToast("评论点赞状态已更新");
  await loadComments();
}

async function login(event) {
  event.preventDefault();
  const result = await api("user", "/user/login", {
    method: "POST",
    body: {
      username: $("#loginUsername").value.trim(),
      password: $("#loginPassword").value,
    },
  });
  const user = result?.data;
  if (!user) throw new Error("登录失败");
  saveSettings({
    userId: user.id,
    username: user.username,
    avatar: user.avatar || "",
    token: user.token || "",
  });
  showToast("登录成功，已更新当前用户");
  fillSettingsForm();
  await loadUsers();
}

async function register(event) {
  event.preventDefault();
  const isAgreeContract = $("#registerAgreement").checked;
  if (!isAgreeContract) {
    $("#registerAgreementError").classList.add("show");
    return;
  }
  $("#registerAgreementError").classList.remove("show");
  await api("user", "/user/register", {
    method: "POST",
    body: {
      username: $("#registerUsername").value.trim(),
      password: $("#registerPassword").value,
      email: $("#registerEmail").value.trim(),
      phone: $("#registerPhone").value.trim(),
      status: 1,
      role: 1,
      isAgreeContract,
    },
  });
  showToast("注册成功");
  $("#registerPanel").reset();
}

async function sendResetCode() {
  const email = $("#resetEmail").value.trim();
  if (!email) {
    showToast("请先输入邮箱");
    return;
  }
  const button = $("#sendResetCode");
  button.disabled = true;
  try {
    const result = await api("user", "/user/sendCode", {
      method: "POST",
      params: { email },
    });
    if (result?.data !== true) {
      throw new Error(result?.message || "验证码发送失败");
    }
    showToast("验证码已发送，请查看邮箱");
    startResetCodeCountdown(60);
  } catch (error) {
    button.disabled = false;
    button.textContent = "发送验证码";
    throw error;
  }
}

function startResetCodeCountdown(seconds) {
  const button = $("#sendResetCode");
  let remain = seconds;
  button.textContent = `${remain}s 后重发`;
  clearInterval(startResetCodeCountdown.timer);
  startResetCodeCountdown.timer = setInterval(() => {
    remain -= 1;
    if (remain <= 0) {
      clearInterval(startResetCodeCountdown.timer);
      button.disabled = false;
      button.textContent = "发送验证码";
      return;
    }
    button.textContent = `${remain}s 后重发`;
  }, 1000);
}

async function resetPassword(event) {
  event.preventDefault();
  const newPassword = $("#resetNewPassword").value;
  const confirmPassword = $("#resetConfirmPassword").value;
  if (newPassword !== confirmPassword) {
    showToast("两次输入的密码不一致");
    return;
  }
  const result = await api("user", "/user/update/user/password", {
    method: "POST",
    body: {
      email: $("#resetEmail").value.trim(),
      verificationCode: $("#resetVerificationCode").value.trim(),
      newPassword,
      confirmPassword,
    },
  });
  if (result?.data !== true) {
    throw new Error(result?.message || "重置密码失败，请检查验证码");
  }
  showToast("密码已重置，请重新登录");
  $("#resetPasswordPanel").reset();
  openUserPanel("loginPanel");
}

async function loadContract() {
  if (state.contract) return state.contract;
  const result = await api("user", "/user/contract");
  state.contract = result?.data || null;
  if (!state.contract) throw new Error("未查询到用户服务协议");
  return state.contract;
}

async function openContract() {
  $("#contractModal").classList.add("open");
  $("#contractModal").setAttribute("aria-hidden", "false");
  $("#contractTitle").textContent = "用户服务协议";
  $("#contractMeta").textContent = "";
  $("#contractContent").textContent = "正在加载协议...";
  try {
    const contract = await loadContract();
    $("#contractTitle").textContent = contract.title || "用户服务协议";
    $("#contractMeta").textContent = `版本：${contract.version || "-"}　更新时间：${contract.updateTime || "-"}`;
    $("#contractContent").textContent = contract.content || "协议内容为空";
  } catch (error) {
    $("#contractContent").textContent = error.message;
  }
}

function closeContract() {
  $("#contractModal").classList.remove("open");
  $("#contractModal").setAttribute("aria-hidden", "true");
}

async function loadUsers() {
  $("#userList").innerHTML = `<div class="empty">查询中...</div>`;
  $("#userTotal").textContent = "查询中";

  try {
    const result = await api("user", "/user/query/user/list", {
      params: {
        PageNum: state.userPage,
        PageSize: state.userPageSize,
        username: $("#userKeyword").value.trim(),
      },
    });
    state.users = result?.data || [];
    state.userTotal = Number(result?.total || 0);
    renderUsers();
  } catch (error) {
    state.users = [];
    state.userTotal = 0;
    $("#userTotal").textContent = "0 人";
    $("#userPageInfo").textContent = "1 / 1";
    $("#userList").innerHTML = `<div class="empty">查询失败：${escapeHtml(error.message)}</div>`;
    throw error;
  }
}

function renderUsers() {
  const pageTotal = totalPage(state.userTotal, state.userPageSize);
  $("#userTotal").textContent = `${state.userTotal} 人`;
  $("#userPageInfo").textContent = `${state.userPage} / ${pageTotal}`;
  $("#userPrev").disabled = state.userPage <= 1;
  $("#userNext").disabled = state.userPage >= pageTotal;

  if (!state.users.length) {
    $("#userList").innerHTML = `<div class="empty">暂无用户</div>`;
    return;
  }

  $("#userList").innerHTML = state.users
    .map(
      (user) => `
        <article class="category-row">
          <div>
            <strong>${escapeHtml(user.username)}</strong>
            <div class="meta">
              <span>ID ${escapeHtml(user.id)}</span>
              <span>${escapeHtml(user.email || "无邮箱")}</span>
              <span>${escapeHtml(user.phone || "无手机")}</span>
              <span>${userStatusLabel(user.status)}</span>
              <span>${roleLabel(user.role)}</span>
            </div>
          </div>
          <div class="card-actions">
            <button data-action="user-edit" data-id="${user.id}">编辑</button>
            <button data-action="user-status" data-id="${user.id}">${Number(user.status) === 2 ? "启用" : "冻结"}</button>
            <button class="danger" data-action="user-delete" data-id="${user.id}">删除</button>
          </div>
        </article>
      `
    )
    .join("");
}

function fillUserForm(user) {
  $("#editUserId").value = user.id || "";
  $("#editUsername").value = user.username || "";
  $("#editEmail").value = user.email || "";
  $("#editPhone").value = user.phone || "";
  $("#editStatus").value = user.status || 1;
  $("#editRole").value = user.role || 1;
  openUserPanel("editUserPanel");
}

async function editUser(id) {
  const result = await api("user", "/user/query/user/info", { params: { id } });
  if (!result?.data) throw new Error("未查询到用户");
  fillUserForm(result.data);
}

async function saveUser(event) {
  event.preventDefault();
  await api("user", "/user/update/user/info", {
    method: "POST",
    body: {
      id: Number($("#editUserId").value),
      username: $("#editUsername").value.trim(),
      email: $("#editEmail").value.trim(),
      phone: $("#editPhone").value.trim(),
      status: Number($("#editStatus").value),
      role: Number($("#editRole").value),
    },
  });
  showToast("用户已保存");
  await loadUsers();
}

async function deleteUser(id) {
  const user = state.users.find((item) => String(item.id) === String(id));
  if (!user || !window.confirm(`确认删除用户 ${user.username}？`)) return;
  await api("user", "/user/delete", {
    method: "POST",
    body: { id: Number(user.id), username: user.username },
  });
  showToast("用户已删除");
  await loadUsers();
}

async function toggleUserStatus(id) {
  const user = state.users.find((item) => String(item.id) === String(id));
  if (!user) return;
  await api("user", "/user/update/user/status", {
    method: "POST",
    body: {
      id: Number(user.id),
      username: user.username,
      status: Number(user.status) === 2 ? 1 : 2,
    },
  });
  showToast("用户状态已更新");
  await loadUsers();
}

function openUserPanel(id) {
  document.querySelectorAll(".tab").forEach((tab) => tab.classList.toggle("active", tab.dataset.userPanel === id));
  document.querySelectorAll(".user-panel").forEach((panel) => panel.classList.toggle("active", panel.id === id));
}

function togglePassword(button) {
  const input = document.getElementById(button.dataset.target);
  if (!input) return;
  const isHidden = input.type === "password";
  input.type = isHidden ? "text" : "password";
  const label = isHidden ? "隐藏密码" : "显示密码";
  button.setAttribute("aria-label", label);
  button.setAttribute("title", label);
  button.querySelector(".eye-icon")?.classList.toggle("closed", !isHidden);
}

function renderCategories() {
  if (!state.categories.length) {
    $("#categoryList").innerHTML = `<div class="empty">暂无分类</div>`;
    return;
  }
  $("#categoryList").innerHTML = state.categories
    .map(
      (item) => `
        <div class="category-row">
          <div>
            <strong>${escapeHtml(item.name)}</strong>
            <div class="muted">ID: ${escapeHtml(item.id)}</div>
          </div>
          <div class="card-actions">
            <button data-action="category-edit" data-id="${item.id}">编辑</button>
            <button class="danger" data-action="category-delete" data-id="${item.id}">删除</button>
          </div>
        </div>
      `
    )
    .join("");
}

function resetCategoryForm() {
  $("#categoryEditId").value = "";
  $("#categoryName").value = "";
}

async function saveCategory(event) {
  event.preventDefault();
  const id = $("#categoryEditId").value;
  const name = $("#categoryName").value.trim();
  if (id) {
    await api("article", "/category/update", { method: "PUT", body: { id: Number(id), name } });
    showToast("分类已更新");
  } else {
    await api("article", "/category/add", { method: "POST", body: { name } });
    showToast("分类已新增");
  }
  resetCategoryForm();
  await loadCategories();
}

function editCategory(id) {
  const category = state.categories.find((item) => String(item.id) === String(id));
  if (!category) return;
  $("#categoryEditId").value = category.id;
  $("#categoryName").value = category.name;
}

async function deleteCategory(id) {
  if (!window.confirm("确认删除这个分类？")) return;
  await api("article", `/category/delete/${id}`, { method: "DELETE" });
  showToast("分类已删除");
  await loadCategories();
}

function fillSettingsForm() {
  $("#articleBase").value = state.settings.articleBase || "";
  $("#commentBase").value = state.settings.commentBase || "";
  $("#userBase").value = state.settings.userBase || "";
  $("#profileUserId").value = state.settings.userId || "";
  $("#profileUsername").value = state.settings.username || "";
  $("#profileToken").value = state.settings.token || "";
  $("#profileAvatar").value = state.settings.avatar || "";
}

function saveSettingsForm(event) {
  event.preventDefault();
  saveSettings({
    articleBase: $("#articleBase").value.trim(),
    commentBase: $("#commentBase").value.trim(),
    userBase: $("#userBase").value.trim(),
    userId: Number($("#profileUserId").value),
    username: $("#profileUsername").value.trim(),
    token: $("#profileToken").value.trim(),
    avatar: $("#profileAvatar").value.trim(),
  });
  fillCommentDefaults();
  showToast("设置已保存");
}

function handleAction(event) {
  const button = event.target.closest("button[data-action]");
  if (!button) return;
  const { action, id } = button.dataset;
  const tasks = {
    "article-detail": () => loadArticleDetail(id),
    "article-edit": () => editArticle(id),
    "article-comments": () => openArticleComments(id),
    "article-like": () => setArticleLike(id),
    "article-collect": () => setArticleCollect(id),
    "article-delete": () => deleteArticle(id),
    "comment-like": () => likeComment(id),
    "comment-delete": () => deleteComment(button),
    "user-edit": () => editUser(id),
    "user-delete": () => deleteUser(id),
    "user-status": () => toggleUserStatus(id),
    "category-edit": () => editCategory(id),
    "category-delete": () => deleteCategory(id),
  };
  const task = tasks[action];
  if (task) Promise.resolve(task()).catch((error) => showToast(error.message));
}

function bindEvents() {
  document.querySelectorAll(".nav-item").forEach((item) => {
    item.addEventListener("click", () => setView(item.dataset.view));
  });
  document.querySelectorAll(".tab").forEach((item) => {
    item.addEventListener("click", () => openUserPanel(item.dataset.userPanel));
  });
  document.querySelectorAll("[data-toggle-password]").forEach((item) => {
    item.addEventListener("click", () => togglePassword(item));
  });

  $("#reloadArticles").addEventListener("click", () => {
    state.articlePage = 1;
    loadArticles().catch((error) => showToast(error.message));
  });
  $("#articleCategoryFilter").addEventListener("change", () => {
    state.articlePage = 1;
    loadArticles().catch((error) => showToast(error.message));
  });
  $("#articleStatusFilter").addEventListener("change", () => {
    state.articlePage = 1;
    loadArticles().catch((error) => showToast(error.message));
  });
  $("#articlePageSize").addEventListener("change", () => {
    state.articlePageSize = Number($("#articlePageSize").value);
    state.articlePage = 1;
    loadArticles().catch((error) => showToast(error.message));
  });
  $("#articlePrev").addEventListener("click", () => {
    state.articlePage = Math.max(1, state.articlePage - 1);
    loadArticles().catch((error) => showToast(error.message));
  });
  $("#articleNext").addEventListener("click", () => {
    state.articlePage += 1;
    loadArticles().catch((error) => showToast(error.message));
  });
  $("#articleForm").addEventListener("submit", (event) => saveArticle(event).catch((error) => showToast(error.message)));
  $("#clearArticleForm").addEventListener("click", () => fillArticleForm());

  $("#reloadComments").addEventListener("click", () => {
    state.commentPage = 1;
    loadComments().catch((error) => showToast(error.message));
  });
  $("#commentPrev").addEventListener("click", () => {
    state.commentPage = Math.max(1, state.commentPage - 1);
    loadComments().catch((error) => showToast(error.message));
  });
  $("#commentNext").addEventListener("click", () => {
    state.commentPage += 1;
    loadComments().catch((error) => showToast(error.message));
  });
  $("#commentForm").addEventListener("submit", (event) => saveComment(event).catch((error) => showToast(error.message)));
  $("#clearCommentForm").addEventListener("click", () => {
    $("#commentContent").value = "";
  });

  $("#loginPanel").addEventListener("submit", (event) => login(event).catch((error) => showToast(error.message)));
  $("#registerPanel").addEventListener("submit", (event) => register(event).catch((error) => showToast(error.message)));
  $("#forgotPassword").addEventListener("click", () => openUserPanel("resetPasswordPanel"));
  $("#sendResetCode").addEventListener("click", () => sendResetCode().catch((error) => showToast(error.message)));
  $("#resetPasswordPanel").addEventListener("submit", (event) => resetPassword(event).catch((error) => showToast(error.message)));
  $("#viewContract").addEventListener("click", () => openContract());
  $("#closeContract").addEventListener("click", closeContract);
  $("#closeContractBackdrop").addEventListener("click", closeContract);
  $("#agreeFromContract").addEventListener("click", () => {
    $("#registerAgreement").checked = true;
    $("#registerAgreementError").classList.remove("show");
    closeContract();
  });
  $("#registerAgreement").addEventListener("change", () => {
    if ($("#registerAgreement").checked) {
      $("#registerAgreementError").classList.remove("show");
    }
  });
  $("#editUserPanel").addEventListener("submit", (event) => saveUser(event).catch((error) => showToast(error.message)));
  $("#reloadUsers").addEventListener("click", () => {
    state.userPage = 1;
    loadUsers().catch((error) => showToast(error.message));
  });
  $("#userKeyword").addEventListener("keydown", (event) => {
    if (event.key === "Enter") {
      event.preventDefault();
      state.userPage = 1;
      loadUsers().catch((error) => showToast(error.message));
    }
  });
  $("#userPrev").addEventListener("click", () => {
    state.userPage = Math.max(1, state.userPage - 1);
    loadUsers().catch((error) => showToast(error.message));
  });
  $("#userNext").addEventListener("click", () => {
    state.userPage += 1;
    loadUsers().catch((error) => showToast(error.message));
  });

  $("#categoryForm").addEventListener("submit", (event) => saveCategory(event).catch((error) => showToast(error.message)));
  $("#clearCategoryForm").addEventListener("click", resetCategoryForm);
  $("#reloadCategories").addEventListener("click", () => loadCategories().catch((error) => showToast(error.message)));
  $("#settingsForm").addEventListener("submit", saveSettingsForm);

  $("#articleList").addEventListener("click", handleAction);
  $("#articleDetail").addEventListener("click", handleAction);
  $("#commentList").addEventListener("click", handleAction);
  $("#userList").addEventListener("click", handleAction);
  $("#categoryList").addEventListener("click", handleAction);
}

async function init() {
  renderApiStatus();
  bindEvents();
  fillSettingsForm();
  fillCommentDefaults();
  await loadCategories();
  fillArticleForm();
  await loadArticles();
}

init().catch((error) => {
  showToast(error.message);
  $("#articleList").innerHTML = `<div class="empty">无法加载数据：${escapeHtml(error.message)}</div>`;
});
