

    public class E24 {
        static int[][] grid = {
                {0, 1, 0},
                {0, 0, 0},
                {0, 1, 0},
        };

        public static void main(String[] args) {
            traverse(0, 0);
            System.out.println(grid[0][0] + "\t" + grid[0][2] + "\t" + grid[2][0] + "\t" + grid[2][2]);
        }

        public static boolean traverse(int row, int column) {
            boolean done = false;
            if (valid(row, column)) {
                grid[row][column] = 2;
                if (row == grid.length - 1 && column == grid[0].length - 1)
                    done = true;
                else {
                    done = traverse(row + 1, column);
                    if (!done)
                        done = traverse(row, column + 1);
                    if (!done)
                        done = traverse(row - 1, column);
                    if (!done)
                        done = traverse(row, column - 1);
                }
                if (done)
                    grid[row][column] = 3;
            }
            return done;
        }

        private static boolean valid(int row, int column) {
            boolean result = false;
            if (row >= 0 && row < grid.length &&
                    column >= 0 && column < grid[row].length)
                if (grid[row][column] == 0)
                    result = true;
            return result;
        }
    }

