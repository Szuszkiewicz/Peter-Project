package Factory;

import com.Peter.enums.ActivityOperationTypeEnums;
import com.Peter.ActivityPostService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ActivityFactory {
    public static final Map<ActivityOperationTypeEnums, ActivityPostService> factory=new ConcurrentHashMap<>();

    public static ActivityPostService fetchActivityService(ActivityOperationTypeEnums activityOperationTypeEnums){
        return factory.get(activityOperationTypeEnums);
    }

    public static void init(ActivityOperationTypeEnums activityOperationTypeEnums,ActivityPostService activityPostService){
        factory.put(activityOperationTypeEnums,activityPostService);

    }


}
