package WizardTD;

public class Button extends App {

    private String content_in_rect;

    private int button_x;
    private final int button_y;

    private final int button_width = 40;
    private final int button_hight = 40;

    private String content_alongSide;

    public Button(String content_in_rect, String content_alongSide, int button_x, int button_y) {
        this.content_in_rect = content_in_rect;
        this.content_alongSide = content_alongSide;
        this.button_x = button_x;
        this.button_y = button_y;
    }

    public int getX() {
        return this.button_x;
    }

    public int getY() {
        return this.button_y;
    }

    // TODO get_which_button()
    // private boolean mouse_on_button()
    public boolean mouse_on_button(App app) {
        int mouseX = app.mouseX;
        int mouseY = app.mouseY;
        if (mouseX <= button_x + button_width &&
                mouseX >= button_x &&
                mouseY >= button_y &&
                mouseY <= button_y + button_hight) {
            return true;

        }
        return false;
    }

    public void drawCost(App app) {
        app.stroke(0);
        app.fill(255);
        app.rect(this.button_x - 65, this.button_y, 60, 20);
        app.stroke(0);
        app.fill(0);
        app.textSize(12);
        app.text("Cost: 300", this.button_x - 65, this.button_y + 15);
    }

    public void drawGrey(App app) {

        // 填充按钮方框颜色与背景颜色融合
        // 鼠标在按钮上变为灰色
        if (mouse_on_button(app)) {
            app.fill(200, 200, 200);
            app.stroke(0);
            app.strokeWeight(1);
            app.rect(button_x, button_y, button_width, button_hight);

        } else {
            app.fill(background_R, background_G, background_B);
            app.stroke(0);
            app.strokeWeight(1);
            app.rect(button_x, button_y, button_width, button_hight);
        }
    }

    public void drawYellow(App app) {

        app.fill(240, 220, 10);
        app.stroke(0);
        app.strokeWeight(1);
        app.rect(button_x, button_y, button_width, button_hight);
        drawText(app);

    }

    public void drawText(App app) {
        // 设置内部字体和说明字体
        app.fill(0);
        app.textSize(25);
        app.text(content_in_rect, button_x + 5, button_y + 30);
        app.textSize(12);
        app.text(content_alongSide, button_x + button_width + 2, button_y + 15);
    }

    public void drawMouse(App app) {
        int mouseX = app.mouseX;
        int mouseY = app.mouseY;
        // 追踪鼠标
        app.stroke(255);
        app.textSize(18);
        app.text("mouseX: " + mouseX / 32 + " mouseY: " + ((mouseY / 32)-1), mouseX - 100, mouseY);

        // 计算鼠标所在的单元格
        int i = mouseX / 32;
        int j = mouseY / 32;

        if (mouseX >= 0 && mouseX <= 640 && mouseY >= TOPBAR && mouseY <= 680) {
            // 高亮显示单元格
            app.noFill(); // 不填充颜色
            app.stroke(255, 0, 0); // 设置边框颜色为红色
            app.strokeWeight(2);
            app.rect(i * 32, j * 32 + 6, 32, 32);
        }

    }

    public void draw(App app) {

        drawGrey(app);
        drawText(app);

        drawMouse(app);
    }
}
