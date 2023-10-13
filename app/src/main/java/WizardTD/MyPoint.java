package WizardTD;
import java.util.Objects;
public class MyPoint {
    
        public int x;
        public int y;
        
        public MyPoint(){}


        public MyPoint(int x, int y)
        {
            this.x = x;
            this.y = y;
        }

        public void setXY(int x, int y){
            this.x = x;
            this.y = y;
        }

        public int getX()
        {
            return this.x;
        }
        public int getY()
        {
            return this.y;
        }

        @Override
        public boolean equals(Object obj)
        {
            MyPoint p = (MyPoint) obj;
            
            return p.x == this.x && p.y == this.y;
        }

        @Override
        public String toString()
        {
            return "X: "+this.x +" "+"Y: "+this.y;
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(x,y);
        }
}
