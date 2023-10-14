package WizardTD;

public class Topbar extends App {
    private final int mana_width = BOARD_WIDTH * CELLSIZE / 2; // 这里是320
    private final int mana_height = 20;
    private final int mana_X = BOARD_WIDTH * CELLSIZE / 2;
    private final int text_y = 30;

    private final int mana_box_x = BOARD_WIDTH * CELLSIZE / 2 + 70;
    private final int mana_box_y = 12;

    private final int mana_info_x = 500;

    public Topbar() {

    }


    public void draw(App app) {
        
        
        // 画一个盖住上方的bar
        app.stroke(background_R, background_G, background_B);
        app.fill(background_R, background_G, background_B);
        app.rect(0, 0, WIDTH, TOPBAR);

        
        int sec = (int) (millis() / 1000);
        int startsIn = app.getStartIn();
        
        if(app.next_wave_display<=waves.size())
        {
            app.textSize(20);
            app.fill(0);
            app.text("Wave: " + app.next_wave_display+ " Starts: " + startsIn, 0, text_y);
        }
        

        // 画背景mana
        app.textSize(20);
        app.fill(0);
        app.text("MANA: ", mana_X, text_y);
        app.stroke(0);
        app.strokeWeight(1);
        app.fill(255);
        app.rect(mana_box_x, mana_box_y, mana_width, mana_height);

        // 画动态，这个mana其实是根据config里面的initial_mana初始的，应该可以在app里面计算然后传过来
        current_mana_value = initial_mana + initial_mana_gained_per_second * sec;
        int mana_inner_width = (int)(((double)current_mana_value/(double)initial_mana_cap) * (double)mana_width);
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
