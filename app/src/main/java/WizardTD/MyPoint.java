package WizardTD;
import java.util.Objects;
/**
 * A class for storing x and y coordinates
 */
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

        /**
         * Override equals to compare in List
         */
        @Override
        public boolean equals(Object obj)
        {
            MyPoint p = (MyPoint) obj;
            
            return p.x == this.x && p.y == this.y;
        }

        /**
         * Override toString() method for print
         */
        @Override
        public String toString()
        {
            return "X: "+this.x +" "+"Y: "+this.y;
        }

        /**
         * Override hashCode to compare
         */
        @Override
        public int hashCode()
        {
            return Objects.hash(x,y);
        }
}
