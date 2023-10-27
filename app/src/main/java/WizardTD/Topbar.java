package WizardTD;

public class Topbar extends App {
    private final int mana_width = BOARD_WIDTH * CELLSIZE / 2;
    private final int mana_height = 20;
    private final int mana_X = BOARD_WIDTH * CELLSIZE / 2;
    private final int text_y = 30;

    private int mana_inner_width;

    private final int mana_box_x = BOARD_WIDTH * CELLSIZE / 2 + 70;
    private final int mana_box_y = 12;

    private final int mana_info_x = 500;

    private int mana_counter = 60;
    public Topbar() {

    }

    /**
     * Control speed of mana_counter for the change of 2 times speed
     * @param sub_value mana value to subtract
     */
    public void mana_counter_sub(int sub_value) {
        mana_counter-= sub_value;
    }
    public void draw(App app) {
        
        // Draw a Topbar
        app.stroke(background_R, background_G, background_B);
        app.fill(background_R, background_G, background_B);
        app.rect(0, 0, WIDTH, TOPBAR);

        int startsIn = app.getStartIn();
        
        if(app.next_wave_display<=waves.size())
        {
            app.textSize(20);
            app.fill(0);
            app.text("Wave: " + app.next_wave_display+ " Starts In: " + startsIn, 0, text_y);
        }
        
        // Draw background mana bar
        app.textSize(20);
        app.fill(0);
        app.text("MANA: ", mana_X, text_y);
        app.stroke(0);
        app.strokeWeight(1);
        app.fill(255);
        app.rect(mana_box_x, mana_box_y, mana_width, mana_height);

        // Update the mana value per second
        if(mana_counter<=0) {
            current_mana_value+=initial_mana_gained_per_second;
            mana_counter=60;
        }

        if (FF_press_condition) {
            mana_counter_sub(2);

        } else {
            mana_counter_sub(1);
        }
        
        // Calculate the proportion of mana_inner_width
        mana_inner_width = (int)(((double)current_mana_value/(double)initial_mana_cap) * (double)mana_width);
        
        // Draw the actual mana bar
        if (current_mana_value <= initial_mana_cap) {
            app.fill(171, 219, 227);
            app.rect(mana_box_x, mana_box_y, mana_inner_width, mana_height);
            app.fill(0);
            app.text(current_mana_value + "/" + initial_mana_cap, mana_info_x, text_y);
        } else {
            app.fill(171, 219, 227);
            app.rect(mana_box_x, mana_box_y, mana_width, mana_height);
            app.fill(0);
            app.text(initial_mana_cap + "/" + initial_mana_cap, mana_info_x, text_y);
        }

    }

}
