import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Represents a single configuration in the skyscraper puzzle.
 *
 * @author RIT CS
 * @author Blake Batson
 */
public class SkyscraperConfig implements Configuration {
    /** empty cell value */
    public final static int EMPTY = 0;

    /** empty cell value display */
    public final static char EMPTY_CELL = '.';

    //Final values to make accessing each border easier
    private final static int NORTH = 0;
    private final static int EAST = 1;
    private final static int SOUTH = 2;
    private final static int WEST = 3;

    private int size;           //The length of each side of the grid
    private int[][] borders;    //2D array storing the border arrays in the same order as the final declares above
    private int[][] grid;       //2D array representing the grid we are filling in

    /**
     * Constructor
     *
     * @param filename the filename
     *  <p>
     *  Read the board file.  It is organized as follows:
     *  DIM     # square DIMension of board (1-9)
     *  lookNS   # DIM values (1-DIM) left to right
     *  lookEW   # DIM values (1-DIM) top to bottom
     *  lookSN   # DIM values (1-DIM) left to right
     *  lookWE   # DIM values (1-DIM) top to bottom
     *  row 1 values    # 0 for empty, (1-DIM) otherwise
     *  row 2 values    # 0 for empty, (1-DIM) otherwise
     *  ...
     *
     *  @throws FileNotFoundException if file not found
     */
    SkyscraperConfig(String filename) throws FileNotFoundException
    {
        Scanner f = new Scanner(new File(filename));

        String[] vals;
        int iLine = 0;
        while(f.hasNextLine())
        {
            String line = f.nextLine();
            if (iLine == 0)     //Get size from line 1, also initialize the arrays
            {
                size = Integer.parseInt(line);
                borders = new int[4][size];
                grid = new int[size][size];
            }
            else if (5 > iLine && iLine > 0)    //Get border values from lines 2 - 5
            {
                vals = line.split(" ");
                for(int i = 0; i < size; i++)
                {
                    borders[iLine-1][i] = Integer.parseInt(vals[i]);
                }
            }
            else if (size + 5 > iLine)      //Get the rest of the lines as the initally filled spaces
            {
                vals = line.split(" ");
                for(int i = 0; i < size; i++)
                {
                    grid[iLine-5][i] = Integer.parseInt(vals[i]);
                }
            }
            iLine++;
        }

        fillKnown();    //Fill the spaces that are easy to fill depending on if the border is 1 or size

        f.close();
    }

    /**
     * Copy constructor
     *
     * @param copy SkyscraperConfig instance
     */
    public SkyscraperConfig(SkyscraperConfig copy)
    {
        this.size = copy.size;
        this.borders = copy.borders;
        this.grid = new int[size][size];
        for(int y = 0; y < size; y++)
        {
            for(int x = 0; x < size; x++)
            {
                this.grid[y][x] = copy.grid[y][x];
            }
        }
    }

    @Override
    public boolean isGoal() {
        //If its valid and theres no free space its the goal
        return isValid() && findFreeSpace()[0] == -1;
    }

    /**
     * getSuccessors
     *
     * @return Collection of Configurations
     */
    @Override
    public Collection<Configuration> getSuccessors()
    {
        int[] free = findFreeSpace();   //Find the available free space
        boolean used = false;
        ArrayList<Configuration> children = new ArrayList<>();

        for (int i = 1; i <= size; i++)
        {
            //Clone this config
            SkyscraperConfig child = new SkyscraperConfig(this);

            //Ensure the number has not been used in this column
            for(int y = 0; y < size; y++)
            {
                if (child.grid[y][free[1]] == i) {
                    used = true;
                    break;
                }
            }
            //Ensure the number has not been used in this row
            for(int x = 0; x < size; x++)
            {
                if (child.grid[free[0]][x] == i) {
                    used = true;
                    break;
                }
            }
            //If the number was used in either dont add it
            if(!used)
            {
                child.grid[free[0]][free[1]] = i;
                children.add(child);
            }
            used = false;
        }
        return children;
    }

    /**
     * isValid() - checks if current config is valid
     *
     * @return true if config is valid, false otherwise
     */
    @Override
    public boolean isValid()
    {
        int count = 0;      //How many buildings are visible from the border position
        int empty = 0;      //How many empty spaces are visible before the tallest tower is seen
        int epast = 0;      //How many empty spaces are visible after the tallest tower is seen
        int tallest = 0;    //What is the tallest tower seen so far
        int self;           //What is the value of the border
        int current;        //What is the value of the space on the grid being evaluated
        boolean pastTallest = false;    //Has the tallest possible tower been passed yet?

        //Check validity of all columns from north border
        for(int x = 0; x < size; x++)
        {
            self = borders[NORTH][x];
            for (int y = 0; y < size; y++)
            {
                current = grid[y][x];   //Get current space

                //If current is empty add to one of the empty counters,
                //else add to count if it's visible and update tallest
                if(current == 0)
                    //Add to empty if haven't passed tallest possible, else add to epast
                    if(pastTallest)
                        epast++;
                    else
                        empty++;
                else if(current > tallest)
                {
                    count++;
                    tallest = current;
                }
                //If this is the tallest tower we have passed the tallest tower
                if(current == size)
                {
                    pastTallest = true;
                }
            }
            //If the values we counted are invalid, return false
            if(invalid(self, count, empty + epast, empty + count))
                return false;
            //Reset values for next iteration
            count = 0;
            empty = 0;
            epast = 0;
            tallest = 0;
            pastTallest = false;
        }

        //Check validity of all rows from east border
        for (int y = 0; y < size; y++)
        {
            self = borders[EAST][y];
            for(int x = size-1; x >= 0; x--)
            {
                current = grid[y][x];   //Get current space

                //If current is empty add to one of the empty counters,
                //else add to count if it's visible and update tallest
                if(current == 0)
                    //Add to empty if haven't passed tallest possible, else add to epast
                    if(pastTallest)
                        epast++;
                    else
                        empty++;
                else if(current > tallest)
                {
                    count++;
                    tallest = current;
                }
                //If this is the tallest tower we have passed the tallest tower
                if(current == size)
                {
                    pastTallest = true;
                }
            }
            //If the values we counted are invalid, return false
            if(invalid(self, count, empty + epast, empty + count))
                return false;
            //Reset values for next iteration
            count = 0;
            empty = 0;
            epast = 0;
            tallest = 0;
            pastTallest = false;
        }

        //Check validity of all columns from south border
        for(int x = 0; x < size; x++)
        {
            self = borders[SOUTH][x];
            for (int y = size-1; y >= 0; y--)
            {
                current = grid[y][x];   //Get current space

                //If current is empty add to one of the empty counters,
                //else add to count if it's visible and update tallest
                if(current == 0)
                    //Add to empty if haven't passed tallest possible, else add to epast
                    if(pastTallest)
                        epast++;
                    else
                        empty++;
                else if(current > tallest)
                {
                    count++;
                    tallest = current;
                }
                //If this is the tallest tower we have passed the tallest tower
                if(current == size)
                {
                    pastTallest = true;
                }
            }
            //If the values we counted are invalid, return false
            if(invalid(self, count, empty + epast, empty + count))
                return false;
            //Reset values for next iteration
            count = 0;
            empty = 0;
            epast = 0;
            tallest = 0;
            pastTallest = false;
        }

        //Check validity of all rows from west border
        for (int y = 0; y < size; y++)
        {
            self = borders[WEST][y];
            for(int x = 0; x < size; x++)
            {
                current = grid[y][x];   //Get current space

                //If current is empty add to one of the empty counters,
                //else add to count if it's visible and update tallest
                if(current == 0)
                    //Add to empty if haven't passed tallest possible, else add to epast
                    if(pastTallest)
                        epast++;
                    else
                        empty++;
                else if(current > tallest)
                {
                    count++;
                    tallest = current;
                }
                //If this is the tallest tower we have passed the tallest tower
                if(current == size)
                {
                    pastTallest = true;
                }
            }
            //If the values we counted are invalid, return false
            if(invalid(self, count, empty + epast, empty + count))
                return false;
            //Reset values for next iteration
            count = 0;
            empty = 0;
            epast = 0;
            tallest = 0;
            pastTallest = false;
        }
        return true;
    }

    /**
     * Validate the count seen from the border against its proper count
     * @param self      How many should be seen
     * @param count     How many are seen
     * @param empty     How many total empty (empty + epast)
     * @param possible  How many possible (count + empty)
     * @return          True if valid config, false otherwise
     */
    private boolean invalid(int self, int count, int empty, int possible)
    {
        //if the counted row / collum is complete, make sure the visible count is exactly the border count
        //else make sure the border count is less than the total of the visible + the empty that could be seen
        if(empty == 0)
        {
            return self != count;
        }
        else return self > possible;
    }

    /**
     * Finds the first free space from the top left to the bottom right
     * @return      an array where 0 is the y value and 1 is the x value,
     *              or and array of -1 at 0 if there is no free space
     */
    private int[] findFreeSpace()
    {
        for (int y = 0; y < size; y++)
        {
            for (int x = 0; x < size; x++)
            {
                if(grid[y][x] == 0)
                    return new int[]{y,x};
            }
        }
        return new int[]{-1};
    }


    /**
     * Fill in the empty values that are always going to be true
     * Call this in constructor to save time
     */
    private void fillKnown()
    {
        //For each border value this checks if it is 1,
        //if it is, make the closest grid space too it the highest possible

        //For each border value if it is the same as the size then fill in the row / col
        //leading away from it with values increasing by 1 from 1 to size

        for (int i = 0; i < size; i++)
        {

            if(borders[NORTH][i] == 1)
                grid[0][i] = size;
            else if(borders[NORTH][i] == size)
            {
                for (int y = 0; y < size; y++)
                {
                    grid[y][i] = y+1;
                }
            }

            if(borders[SOUTH][i] == 1)
                grid[size-1][i] = size;
            else if(borders[SOUTH][i] == size)
            {
                for (int y = size-1; y >= 0; y--)
                {
                    grid[y][i] = size - y;
                }
            }

            if(borders[WEST][i] == 1)
                grid[i][0] = size;
            else if(borders[WEST][i] == size)
            {
                for (int x = 0; x < size; x++)
                {
                    grid[i][x] = x+1;
                }
            }

            if(borders[EAST][i] == 1)
                grid[i][size-1] = size;
            else if(borders[EAST][i] == size)
            {
                for (int x = size-1; x >= 0; x--)
                {
                    grid[i][x] = size - x;
                }
            }
        }
    }

    /**
     * toString() method
     *
     * @return String representing configuration board & grid w/ look values.
     * The format of the output for the problem solving initial config is:
     *
     *   1 2 4 2
     *   --------
     * 1|. . . .|3
     * 2|. . . .|3
     * 3|. . . .|1
     * 3|. . . .|2
     *   --------
     *   4 2 1 2
     */
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder("  "); //We are building a new string
        int place = 0;  // used to keep track of the actual index for writing north and south borders

        //North bar
        for( int i = 0; i < size * 2 - 1; i++)
        {
            //Write the value if the loop is even, add a space otherwise
            if(i % 2 == 0)
            {
                out.append(borders[NORTH][place]);
                place++;
            }
            else
                out.append(" ");
        }
        out.append("%n  ");

        //North seperator
        out.append("-".repeat(Math.max(0, size * 2)));
        out.append("%n");

        //West border, grid, right border
        for (int i = 0; i < size; i++)
        {
            //Write the west and the seperator
            out.append(borders[WEST][i]).append("|");
            //Fill in all the values of the grid at that y followed by a space
            for (int j = 0; j < size; j++)
            {
                if(grid[i][j] == EMPTY)
                    out.append(EMPTY_CELL + " ");
                else
                    out.append(grid[i][j]).append(" ");
            }
            out = new StringBuilder(out.substring(0, out.length() - 1));    //take off the last space
            out.append("|").append(borders[EAST][i]).append("%n");  //write the right seperator and value
        }

        //South seperator
        out.append("  ");
        out.append("-".repeat(Math.max(0, size * 2)));
        out.append("%n  ");

        place = 0;
        //South bar
        for( int i = 0; i < size * 2 - 1; i++)
        {
            //Write the value if the loop is even, add a space otherwise
            if(i % 2 == 0)
            {
                out.append(borders[SOUTH][place]);
                place++;
            }
            else
                out.append(" ");
        }

        return String.format(out.toString());  // remove
    }
}
