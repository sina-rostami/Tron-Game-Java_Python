package ai;

import ks.KSObject;
import ks.commands.ActivateWallBreaker;
import ks.commands.ChangeDirection;
import ks.commands.ECommandDirection;
import ks.models.ECell;
import ks.models.EDirection;
import ks.models.World;
import team.koala.chillin.client.RealtimeAI;

import java.util.ArrayList;


public class AI extends RealtimeAI<World, KSObject> {
    private EDirection dir;
    private ECell enemyCell;
    private ECell myCell;

    public AI(World world) {
        super(world);
    }

    @Override
    public void initialize() {

        if (world.getBoard().get(world.getAgents().get(this.mySide).getPosition().getY() - 1).get(world.getAgents().get(this.mySide).getPosition().getX()) == ECell.AreaWall)
            dir = EDirection.Down;
        else
            dir = EDirection.Up;

        changeDirection(dir);
        enemyCell = mySide.equals("Yellow") ? ECell.BlueWall : ECell.YellowWall;
        myCell = mySide.equals("Yellow") ? ECell.YellowWall : ECell.BlueWall;

        System.out.println("initialize");
    }
    // todo : add bfs to find nearest enemy wall

    // todo : dfs time
    // todo : add cooldown to dfs
    // todo : suicide when ahead
    // todo : go for shakh to shack when ahead
    // todo : check shakh to shakh
    // todo : best first search
    // todo : add shakh to shakh to dfs
    // todo : go to more empty places


    @Override
    public void decide() {
        int currentX = world.getAgents().get(this.mySide).getPosition().getX();
        int currentY = world.getAgents().get(this.mySide).getPosition().getY();
        int wallBeakerRem = world.getConstants().getWallBreakerDuration();
        dir = world.getAgents().get(mySide).getDirection();

        if (world.getAgents().get(mySide).getWallBreakerRemTime() != 0)
            wallBeakerRem = world.getAgents().get(mySide).getWallBreakerRemTime();
        else if (world.getAgents().get(mySide).getWallBreakerCooldown() <= 6 && world.getAgents().get(mySide).getWallBreakerCooldown() != 0)
            wallBeakerRem = 0;


        DFS(currentY, currentX, wallBeakerRem, 0, 0, world.getAgents().get(mySide).getHealth(), dir);

        int[] arr = NearestEnemyWall(currentX, currentY);
        System.out.println(arr[0] + " , " + arr[1]);


        if (world.getScores().get(mySide) <= world.getScores().get(otherSide))
            HeadToHeadCheck(currentX, currentY);

        WallBeakerCheck(currentX, currentY);

        changeDirection(dir);
    }

    private void HeadToHeadCheck(int currentX, int currentY) {
        if (dir == EDirection.Down) currentY += 1;
        if (dir == EDirection.Up) currentY -= 1;
        if (dir == EDirection.Right) currentX += 1;
        if (dir == EDirection.Left) currentX -= 1;

        if ((world.getAgents().get(otherSide).getPosition().getY() + 1 == currentY || world.getAgents().get(otherSide).getPosition().getY() - 1 == currentY) && world.getAgents().get(otherSide).getPosition().getX() == currentX) {
            currentX = world.getAgents().get(this.mySide).getPosition().getX();
            currentY = world.getAgents().get(this.mySide).getPosition().getY();
            if (dir != EDirection.Down && world.getAgents().get(mySide).getDirection() != EDirection.Up && world.getBoard().get(currentY + 1).get(currentX) == ECell.Empty)
                dir = EDirection.Down;
            else if (dir != EDirection.Up && world.getAgents().get(mySide).getDirection() != EDirection.Down && world.getBoard().get(currentY - 1).get(currentX) == ECell.Empty)
                dir = EDirection.Up;
            else if (dir != EDirection.Left && world.getAgents().get(mySide).getDirection() != EDirection.Right && world.getBoard().get(currentY).get(currentX - 1) == ECell.Empty)
                dir = EDirection.Left;
            else if (dir != EDirection.Right && world.getAgents().get(mySide).getDirection() != EDirection.Left && world.getBoard().get(currentY).get(currentX + 1) == ECell.Empty)
                dir = EDirection.Right;
            return;
        }
        if ((world.getAgents().get(otherSide).getPosition().getX() + 1 == currentX || world.getAgents().get(otherSide).getPosition().getX() - 1 == currentX) && world.getAgents().get(otherSide).getPosition().getY() == currentY) {
            currentX = world.getAgents().get(this.mySide).getPosition().getX();
            currentY = world.getAgents().get(this.mySide).getPosition().getY();
            if (dir != EDirection.Down && world.getAgents().get(mySide).getDirection() != EDirection.Up && world.getBoard().get(currentY + 1).get(currentX) == ECell.Empty)
                dir = EDirection.Down;
            else if (dir != EDirection.Up && world.getAgents().get(mySide).getDirection() != EDirection.Down && world.getBoard().get(currentY - 1).get(currentX) == ECell.Empty)
                dir = EDirection.Up;
            else if (dir != EDirection.Left && world.getAgents().get(mySide).getDirection() != EDirection.Right && world.getBoard().get(currentY).get(currentX - 1) == ECell.Empty)
                dir = EDirection.Left;
            else if (dir != EDirection.Right && world.getAgents().get(mySide).getDirection() != EDirection.Left && world.getBoard().get(currentY).get(currentX + 1) == ECell.Empty)
                dir = EDirection.Right;
            return;
        }

        if ((world.getAgents().get(otherSide).getPosition().getY() + 1 == currentY || world.getAgents().get(otherSide).getPosition().getY() - 1 == currentY) && world.getAgents().get(otherSide).getPosition().getX() == currentX) {
            currentX = world.getAgents().get(this.mySide).getPosition().getX();
            currentY = world.getAgents().get(this.mySide).getPosition().getY();
            if (dir != EDirection.Down && world.getAgents().get(mySide).getDirection() != EDirection.Up && world.getBoard().get(currentY + 1).get(currentX) != ECell.AreaWall)
                dir = EDirection.Down;
            else if (dir != EDirection.Up && world.getAgents().get(mySide).getDirection() != EDirection.Down && world.getBoard().get(currentY - 1).get(currentX) != ECell.AreaWall)
                dir = EDirection.Up;
            else if (dir != EDirection.Left && world.getAgents().get(mySide).getDirection() != EDirection.Right && world.getBoard().get(currentY).get(currentX - 1) != ECell.AreaWall)
                dir = EDirection.Left;
            else if (dir != EDirection.Right && world.getAgents().get(mySide).getDirection() != EDirection.Left && world.getBoard().get(currentY).get(currentX + 1) != ECell.AreaWall)
                dir = EDirection.Right;
            return;
        }
        if ((world.getAgents().get(otherSide).getPosition().getX() + 1 == currentX || world.getAgents().get(otherSide).getPosition().getX() - 1 == currentX) && world.getAgents().get(otherSide).getPosition().getY() == currentY) {
            currentX = world.getAgents().get(this.mySide).getPosition().getX();
            currentY = world.getAgents().get(this.mySide).getPosition().getY();
            if (dir != EDirection.Down && world.getAgents().get(mySide).getDirection() != EDirection.Up && world.getBoard().get(currentY + 1).get(currentX) != ECell.AreaWall)
                dir = EDirection.Down;
            else if (dir != EDirection.Up && world.getAgents().get(mySide).getDirection() != EDirection.Down && world.getBoard().get(currentY - 1).get(currentX) != ECell.AreaWall)
                dir = EDirection.Up;
            else if (dir != EDirection.Left && world.getAgents().get(mySide).getDirection() != EDirection.Right && world.getBoard().get(currentY).get(currentX - 1) != ECell.AreaWall)
                dir = EDirection.Left;
            else if (dir != EDirection.Right && world.getAgents().get(mySide).getDirection() != EDirection.Left && world.getBoard().get(currentY).get(currentX + 1) != ECell.AreaWall)
                dir = EDirection.Right;
            return;
        }
        return;
    }

    private void WallBeakerCheck(int currentX, int currentY) {
        if (dir == EDirection.Up && world.getBoard().get(currentY - 1).get(currentX) != ECell.Empty)
            ActivateWallBreaker();
        if (dir == EDirection.Down && world.getBoard().get(currentY + 1).get(currentX) != ECell.Empty)
            ActivateWallBreaker();
        if (dir == EDirection.Left && world.getBoard().get(currentY).get(currentX - 1) != ECell.Empty)
            ActivateWallBreaker();
        if (dir == EDirection.Right && world.getBoard().get(currentY).get(currentX + 1) != ECell.Empty)
            ActivateWallBreaker();
    }

    public int DFS(int locationY, int locationX, int wallBrakeRem, int score, int movesCnt,
                   int health, EDirection lastDirection) {
        if (locationX < 1 || locationX >= world.getBoard().get(0).size() ||
                locationY < 1 || locationY >= world.getBoard().size())
            return -1;

        if (world.getBoard().get(locationY).get(locationX) == ECell.AreaWall) {
            return 0;
        }

        if (movesCnt == 13) {
            if (world.getBoard().get(locationY).get(locationX) != ECell.Empty)
                return -3000;
            return score;
        }

        if (wallBrakeRem > 0 && (world.getBoard().get(locationY).get(locationX) != ECell.Empty || wallBrakeRem < world.getConstants().getWallBreakerDuration()))
            --wallBrakeRem;
        else if (health > 0 && world.getBoard().get(locationY).get(locationX) != ECell.Empty) {
            --health;
            score -= 15;
        }

        if (health == 0) {
            if (world.getBoard().get(locationY).get(locationX) == enemyCell)
                score += world.getConstants().getEnemyWallCrashScore();
            else score += world.getConstants().getMyWallCrashScore();
            return score;
        }

        int down = -30000, up = -30000, right = -30000, left = -30000;

        // change score
        if (world.getBoard().get(locationY).get(locationX) == enemyCell) score += 6;
        if (world.getBoard().get(locationY).get(locationX) == ECell.Empty) score += 1;
        if (world.getBoard().get(locationY).get(locationX) == myCell) score -= 2;


        // check down
        if (lastDirection != EDirection.Up && world.getBoard().get(locationY + 1).get(locationX) != ECell.AreaWall) {
            down = DFS(locationY + 1, locationX, wallBrakeRem, score, movesCnt + 1, health, EDirection.Down);
        }

        //check up
        if (lastDirection != EDirection.Down && world.getBoard().get(locationY - 1).get(locationX) != ECell.AreaWall) {
            up = DFS(locationY - 1, locationX, wallBrakeRem, score, movesCnt + 1, health, EDirection.Up);
        }

        //check right
        if (lastDirection != EDirection.Left && world.getBoard().get(locationY).get(locationX + 1) != ECell.AreaWall) {
            right = DFS(locationY, locationX + 1, wallBrakeRem, score, movesCnt + 1, health, EDirection.Right);
        }

        //check left
        if (lastDirection != EDirection.Right && world.getBoard().get(locationY).get(locationX - 1) != ECell.AreaWall) {
            left = DFS(locationY, locationX - 1, wallBrakeRem, score, movesCnt + 1, health, EDirection.Left);
        }

        if (movesCnt == 0) DFSDirection(up, down, left, right);

        return maxThree(down, up, right, left);
    }


    private void DFSDirection(int up, int down, int left, int right) {
        EDirection nearestEnemyDir = findNearestEnemyWall(world.getAgents().get(mySide).getPosition().getX(), world.getAgents().get(mySide).getPosition().getY());

        if (up >= maxThree(up, right, down, left) && nearestEnemyDir == EDirection.Up) {
            dir = EDirection.Up;
            return;
        } else if (down >= maxThree(up, right, down, left) && nearestEnemyDir == EDirection.Down) {
            dir = EDirection.Down;
            return;
        } else if (left >= maxThree(up, right, down, left) && nearestEnemyDir == EDirection.Left) {
            dir = EDirection.Left;
            return;
        } else if (right >= maxThree(up, right, down, left) && nearestEnemyDir == EDirection.Right) {
            dir = EDirection.Right;
            return;
        }

        if (up >= maxThree(up, right, down, left)) dir = EDirection.Up;
        else if (down >= maxThree(up, right, down, left)) dir = EDirection.Down;
        else if (left >= maxThree(up, right, down, left)) dir = EDirection.Left;
        else if (right >= maxThree(up, right, down, left)) dir = EDirection.Right;

    }


    private int[] NearestEnemyWall(int currentX, int currentY) {

        ArrayList<ECell> queue = new ArrayList<>();

        queue.add(world.getBoard().get(currentY).get(currentX));


        while (!queue.isEmpty()) {
            if (queue.get(0) == enemyCell) {

                // done
            }


        }


        return null;
    }


    private EDirection findNearestEnemyWall(int currentX, int currentY) {
        int nearestX = 1, nearestY = 1, minDistance = 10000000;

        for (int i = 0; i < world.getBoard().size(); i++) {
            for (int j = 0; j < world.getBoard().get(0).size(); j++) {
                int distance = (i - currentY) * (i - currentY) + (j - currentX) * (j - currentX);
                if (world.getBoard().get(i).get(j) == enemyCell && distance < minDistance) {
                    nearestY = i;
                    nearestX = j;
                    minDistance = distance;
                }
            }
        }

        if (nearestY > currentY && world.getBoard().get(currentY + 1).get(currentX) != ECell.AreaWall && dir != EDirection.Up)
            return EDirection.Down;

        if (nearestY < currentY && world.getBoard().get(currentY - 1).get(currentX) != ECell.AreaWall && dir != EDirection.Down)
            return EDirection.Up;

        if (nearestX > currentX && world.getBoard().get(currentY).get(currentX + 1) != ECell.AreaWall && dir != EDirection.Left)
            return EDirection.Right;

        if (nearestX < currentX && world.getBoard().get(currentY).get(currentX - 1) != ECell.AreaWall && dir != EDirection.Right)
            return EDirection.Left;


        // make sure
        if (nearestY >= currentY && world.getBoard().get(currentY + 1).get(currentX) != ECell.AreaWall && dir != EDirection.Up)
            return EDirection.Down;

        if (nearestY <= currentY && world.getBoard().get(currentY - 1).get(currentX) != ECell.AreaWall && dir != EDirection.Down)
            return EDirection.Up;

        if (nearestX >= currentX && world.getBoard().get(currentY).get(currentX + 1) != ECell.AreaWall && dir != EDirection.Left)
            return EDirection.Right;

        if (nearestX <= currentX && world.getBoard().get(currentY).get(currentX - 1) != ECell.AreaWall && dir != EDirection.Right)
            return EDirection.Left;

        return dir;
    }

    public void changeDirection(EDirection direction) {
        final ECommandDirection dir = ECommandDirection.of(direction.getValue());
        this.sendCommand(new ChangeDirection() {
            {
                direction = dir;
            }
        });
    }

    public void ActivateWallBreaker() {
        this.sendCommand(new ActivateWallBreaker());
    }

    private int maxThree(int a, int b, int c, int d) {
        if (a >= b && a >= c && a >= d) return a;
        if (b >= a && b >= c && b >= d) return b;
        if (c >= a && c >= b && c >= d) return c;
        return d;
    }

}