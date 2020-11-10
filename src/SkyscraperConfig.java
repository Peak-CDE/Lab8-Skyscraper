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
        this.grid = copy.grid;
    }

    @Override
    public boolean isGoal() {
        if (isValid() && findFreeSpace()[0] == -1)
            return true;
        else return false;
    }

    /**
     * getSuccessors
     *
     * @returns Collection of Configurations
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
            if(child.isGoal())
                break;
            for(int y = 0; y < size; y++)
            {
                if(child.grid[y][free[1]] == i)
                    used = true;

            }
            for(int x = 0; x < size; x++)
            {
                if(child.grid[free[0]][x] == i)
                    used = true;
            }
            if(!used && child.isValid())
            {
                child.grid[free[0]][free[1]] = i;
                children.add(child);
                children.addAll(child.getSuccessors());
            }
            used = false;
        }
        return children;
    }

    /**
     * isValid() - checks if current config is valid
     *
     * @returns true if config is valid, false otherwise
     */
    @Override
    public boolean isValid()
    {
        int count = 0;
        int empty = 0;
        int tallest = 0;
        int self;
        int current;

        for(int x = 0; x < size; x++)
        {
            self = borders[NORTH][x];
            for (int y = 0; y < size; y++)
            {
                current = grid[y][x];

                if(current == 0)
                    empty++;
                else if(current > tallest)
                {
                    count++;
                    tallest = current;
                }
                if(current == size)
                {
                    break;
                }
            }
            if(self > count + empty)
            {
                System.out.println("\nERROR\n" + this + "\nNorth Wanted:" + self + " Got:" + (count+empty));
                return false;
            }
            count = 0;
            empty = 0;
            tallest = 0;
        }

        for (int y = 0; y < size; y++)
        {
            self = borders[EAST][y];
            for(int x = size-1; x >= 0; x--)
            {
                current = grid[y][x];

                if(current == 0)
                    empty++;
                else if(current > tallest)
                {
                    count++;
                    tallest = current;
                }
                if(current == size)
                {
                    break;
                }
            }
            if(self > count + empty)
            {
                System.out.println("\nERROR\n" + this + "\nEast Wanted:" + self + " Got:" + (count+empty));
                return false;
            }
            count = 0;
            empty = 0;
            tallest = 0;
        }

        for(int x = 0; x < size; x++)
        {
            self = borders[SOUTH][x];
            for (int y = size-1; y >= 0; y--)
            {
                current = grid[y][x];

                if(current == 0)
                    empty++;
                else if(current > tallest)
                {
                    count++;
                    tallest = current;
                }
                if(current == size)
                {
                    break;
                }
            }
            if(self > count + empty)
            {
                System.out.println("\nERROR\n" + this + "\nSouth Wanted:" + self + " Got:" + (count+empty));
                return false;
            }
            count = 0;
            empty = 0;
            tallest = 0;
        }

        for (int y = 0; y < size; y++)
        {
            self = borders[WEST][y];
            for(int x = 0; x < size; x++)
            {
                current = grid[y][x];

                if(current == 0)
                    empty++;
                else if(current > tallest)
                {
                    count++;
                    tallest = current;
                }
                if(current == size)
                {
                    break;
                }
            }
            if(self > count + empty)
            {
                System.out.println("\nERROR\n" + this + "\nWest Wanted:" + self + " Got:" + (count+empty));
                return false;
            }
            count = 0;
            empty = 0;
            tallest = 0;
        }
        /*
        for(int x = 0; x < size; x++)
        {
        }
        for (int y = 0; y < size; y++)
        {
        }
        */
        System.out.println("\nVALID\n" + this);
        return true;
    }

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

            if(borders[SOUTH][i] == 1)
                grid[size-1][i] = size;

            if(borders[WEST][i] == 1)
                grid[i][0] = size;

            if(borders[EAST][i] == 1)
                grid[i][size-1] = size;
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
        String out = "  ";
        int place = 0;

        //North bar
        for( int i = 0; i < size * 2 - 1; i++)
        {
            if(i % 2 == 0)
            {
                out += borders[NORTH][place];
                place++;
            }
            else
                out += " ";
        }
        out += "%n  ";

        //North seperator
        for(int i = 0; i < size*2; i++)
            out += "-";
        out += "%n";

        //West border, grid, right border
        for (int i = 0; i < size; i++)
        {
            out += borders[WEST][i] + "|";
            for (int j = 0; j < size; j++)
            {
                if(grid[i][j] == EMPTY)
                    out += EMPTY_CELL + " ";
                else
                    out += grid[i][j] + " ";
            }
            out = out.substring(0, out.length()-1);
            out += "|" + borders[EAST][i] + "%n";
        }

        //South seperator
        out += "  ";
        for(int i = 0; i < size*2; i++)
            out += "-";
        out += "%n  ";

        place = 0;
        //South bar
        for( int i = 0; i < size * 2 - 1; i++)
        {
            if(i % 2 == 0)
            {
                out += borders[SOUTH][place];
                place++;
            }
            else
                out += " ";
        }

        return String.format(out);  // remove
    }
}
