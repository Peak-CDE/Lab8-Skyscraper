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

    private final static int NORTH = 0;
    private final static int EAST = 1;
    private final static int SOUTH = 2;
    private final static int WEST = 3;

    private int size;
    private int[][] borders;
    private int[][] grid;

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
            if (iLine == 0)
            {
                size = Integer.parseInt(line);
                borders = new int[4][size];
                grid = new int[size][size];
            }
            else if (5 > iLine && iLine > 0)
            {
                vals = line.split(" ");
                for(int i = 0; i < size; i++)
                {
                    borders[iLine-1][i] = Integer.parseInt(vals[i]);
                }
            }
            else if (size + 5 > iLine)
            {
                vals = line.split(" ");
                for(int i = 0; i < size; i++)
                {
                    grid[iLine-5][i] = Integer.parseInt(vals[i]);
                }
            }
            iLine++;
        }

        fillKnown();

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
        int[] free = findFreeSpace();
        boolean used = false;
        ArrayList<Configuration> children = new ArrayList<>();

        for (int i = 1; i <= size; i++)
        {
            SkyscraperConfig child = new SkyscraperConfig(this);

            for(int y = 0; y < size; y++)
            {
                if (child.grid[y][free[1]] == i) {
                    used = true;
                    break;
                }
            }
            for(int x = 0; x < size; x++)
            {
                if (child.grid[free[0]][x] == i) {
                    used = true;
                    break;
                }
            }
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
        int count = 0;
        int empty = 0;
        int epast = 0;
        int tallest = 0;
        int self;
        int current;
        boolean pastTallest = false;

        for(int x = 0; x < size; x++)
        {
            self = borders[NORTH][x];
            for (int y = 0; y < size; y++)
            {
                current = grid[y][x];

                if(current == 0)
                    if(pastTallest)
                        epast++;
                    else
                        empty++;
                else if(current > tallest)
                {
                    count++;
                    tallest = current;
                }
                if(current == size)
                {
                    pastTallest = true;
                }
            }
            if(invalid(self, count, empty + epast, empty + count))
                return false;
            count = 0;
            empty = 0;
            epast = 0;
            tallest = 0;
            pastTallest = false;
        }

        for (int y = 0; y < size; y++)
        {
            self = borders[EAST][y];
            for(int x = size-1; x >= 0; x--)
            {
                current = grid[y][x];

                if(current == 0)
                    if(pastTallest)
                        epast++;
                    else
                        empty++;
                else if(current > tallest)
                {
                    count++;
                    tallest = current;
                }
                if(current == size)
                {
                    pastTallest = true;
                }
            }
            if(invalid(self, count, empty + epast, empty + count))
                return false;
            count = 0;
            empty = 0;
            epast = 0;
            tallest = 0;
            pastTallest = false;
        }

        for(int x = 0; x < size; x++)
        {
            self = borders[SOUTH][x];
            for (int y = size-1; y >= 0; y--)
            {
                current = grid[y][x];

                if(current == 0)
                    if(pastTallest)
                        epast++;
                    else
                        empty++;
                else if(current > tallest)
                {
                    count++;
                    tallest = current;
                }
                if(current == size)
                {
                    pastTallest = true;
                }
            }
            if(invalid(self, count, empty + epast, empty + count))
                return false;
            count = 0;
            empty = 0;
            epast = 0;
            tallest = 0;
            pastTallest = false;
        }

        for (int y = 0; y < size; y++)
        {
            self = borders[WEST][y];
            for(int x = 0; x < size; x++)
            {
                current = grid[y][x];

                if(current == 0)
                    if(pastTallest)
                        epast++;
                    else
                        empty++;
                else if(current > tallest)
                {
                    count++;
                    tallest = current;
                }
                if(current == size)
                {
                    pastTallest = true;
                }
            }
            if(invalid(self, count, empty + epast, empty + count))
                return false;
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

    private void fillKnown()
    {
        for (int i = 0; i < size; i++)
        {
            //Max size has to be next to it
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
        StringBuilder out = new StringBuilder("  ");
        int place = 0;

        //North bar
        for( int i = 0; i < size * 2 - 1; i++)
        {
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
            out.append(borders[WEST][i]).append("|");
            for (int j = 0; j < size; j++)
            {
                if(grid[i][j] == EMPTY)
                    out.append(EMPTY_CELL + " ");
                else
                    out.append(grid[i][j]).append(" ");
            }
            out = new StringBuilder(out.substring(0, out.length() - 1));
            out.append("|").append(borders[EAST][i]).append("%n");
        }

        //South seperator
        out.append("  ");
        out.append("-".repeat(Math.max(0, size * 2)));
        out.append("%n  ");

        place = 0;
        //South bar
        for( int i = 0; i < size * 2 - 1; i++)
        {
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
