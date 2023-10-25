package WizardTD;

public class Button extends App {

    private int button_cost;
    private String content_in_rect;
    private String content_alongSide;
    private int button_x;
    private final int button_y;

    private final int button_width = 40;
    private final int button_hight = 40;

    

    public Button(int button_cost, String content_in_rect, String content_alongSide, int button_x, int button_y) {
        this.button_cost = button_cost;
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

    /**
     * Detect whether cursor is on button
     * @param app App class object
     * @return cursor on button condition
     */
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

    /**
     * Draw button cost rectangle
     * @param app App class object
     */
    public void drawCost(App app) {
        app.stroke(0);
        app.fill(255);
        app.rect(this.button_x - 65, this.button_y, 60, 20);
        app.stroke(0);
        app.fill(0);
        app.textSize(12);
        app.text("Cost: "+this.button_cost, this.button_x - 65, this.button_y + 15);
    }

    /**
     * Draw button cost value
     * @param app App class object
     * @param value cost value
     */
    public void drawCostValue(App app, int value) {
        app.stroke(0);
        app.fill(255);
        app.rect(this.button_x - 65, this.button_y, 60, 20);
        app.stroke(0);
        app.fill(0);
        app.textSize(12);
        app.text("Cost: "+value, this.button_x - 65, this.button_y + 15);
    }

    /**
     * Fill the button with grey
     * @param app
     */
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

    /**
     * Fill the button with yellow
     * @param app
     */
    public void drawYellow(App app) {

        app.fill(240, 220, 10);
        app.stroke(0);
        app.strokeWeight(1);
        app.rect(button_x, button_y, button_width, button_hight);
        drawText(app);

    }

    /**
     * Draw texts in and alongside button
     * @param app
     */
    public void drawText(App app) {
        // 设置内部字体和说明字体
        app.fill(0);
        app.textSize(25);
        app.text(content_in_rect, button_x + 5, button_y + 30);
        app.textSize(12);
        app.text(content_alongSide, button_x + button_width + 2, button_y + 15);
    }

    /**
     * Cursor enhancement
     * @param app
     */
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
