package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.MouseEvent;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;

public class App extends PApplet {

    public static final int CELLSIZE = 32;
    public static final int SIDEBAR = 120;
    public static final int TOPBAR = 40;
    public static final int BOARD_WIDTH = 20;
    public int background_R = 132;
    public int background_G = 115;
    public int background_B = 74;

    public static int WIDTH = CELLSIZE * BOARD_WIDTH + SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH * CELLSIZE + TOPBAR;

    public static final int FPS = 60;

    public String configPath;

    public Random random = new Random();

    public PImage[][] mapTile = new PImage[20][20];;
    public static char[][] Map_Grid;

    private static int wizard_house_display_x;
    private static int wizard_house_display_y;
    public static int wizard_house_x;
    public static int wizard_house_y;
    private static PImage wizardImage;

    private Topbar topbar;
    private Sidebar sidebar;

    public static HashMap<MyPoint, ArrayList<MyPoint>> shortest_Paths = new HashMap<>();

    // public static ArrayList<ArrayList<MyPoint>> shortest_Paths = new
    // ArrayList<>();
    public static ArrayList<MyPoint> startPoints = new ArrayList<>();

    // Feel free to add any additional methods or attributes you want. Please put
    // classes in different files.

    // 读取level文件并存储进20x20的数组
    public char[][] readLevel(String fileAddress) {
        char[][] data = new char[20][20];

        // 先全部填充Grass
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                data[i][j] = ' ';
            }
        }

        try {
            File file = new File(fileAddress);
            Scanner sc = new Scanner(file);

            // System.out.println(row);
            for (int x = 0; x < 20; x++) {
                String row = sc.nextLine();
                for (int y = 0; y < row.length(); y++) {
                    data[x][y] = row.charAt(y);
                }
            }

            sc.close();
            return data;

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return null;
        }

    }

    // 选择path的方向,在loadBackGround()中使用
    public int choosePath(int x, int y, char[][] backGroundArray) {
        int upX = x - 1;
        int downX = x + 1;
        int leftY = y - 1;
        int rightY = y + 1;

        // int upY = y - 1;
        // int downY = y + 1;
        // int leftX = x - 1;
        // int rightX = x + 1;

        char up = ' ';
        char down = ' ';
        char left = ' ';
        char right = ' ';

        // 以下四个try catch分别捕捉上下左右四个异常并处理,如无异常，正常赋值
        try {
            up = backGroundArray[upX][y];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("上边界出界: " + upX + " " + y);
            startPoints.add(new MyPoint(upX + 1, y));
            up = 'X';
        }
        try {
            down = backGroundArray[downX][y];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("下边界出界： " + downX + " " + y);
            startPoints.add(new MyPoint(downX, y));
            down = 'X';

        }
        try {
            left = backGroundArray[x][leftY];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("左边界出界: " + x + " " + leftY);
            startPoints.add(new MyPoint(x, leftY + 1));
            left = 'X';
        }
        try {
            right = backGroundArray[x][rightY];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("右边界出界： " + x + " " + rightY);
            startPoints.add(new MyPoint(x, rightY));
            right = 'X';
        }

        // try {
        // up = backGroundArray[x][upY];
        // } catch (ArrayIndexOutOfBoundsException e) {
        // System.out.println("上边界出界: " + x + " " + upY);
        // up = 'X';
        // }
        // try {
        // down = backGroundArray[x][downY];
        // } catch (ArrayIndexOutOfBoundsException e) {
        // System.out.println("下边界出界： " + x + " " + downY);
        // down = 'X';

        // }
        // try {
        // left = backGroundArray[leftX][y];
        // } catch (ArrayIndexOutOfBoundsException e) {
        // System.out.println("左边界出界: " + leftX + " " + y);
        // left = 'X';
        // }
        // try {
        // right = backGroundArray[rightX][y];
        // } catch (ArrayIndexOutOfBoundsException e) {
        // System.out.println("右边界出界： " + rightX + " " + y);
        // right = 'X';
        // }

        // 计算上下左右总值
        int fourValue = (int) up + (int) down + (int) left + (int) right;

        // 上草下路用右拐
        if (down == 'X' && left == 'X' && up != 'X' && right != 'X') {
            return 1;
        }
        if (up == 'X' && left == 'X' && down != 'X' && right != 'X') {
            return 11;
        }
        if (up == 'X' && right == 'X' && down != 'X' && left != 'X') {
            return 12;
        }
        if (down == 'X' && right == 'X' && up != 'X' && left != 'X') {
            return 13;
        }

        // T字路：单面为Shrub或者Grass
        int T_road1 = 296;
        int T_road2 = 347;
        if (fourValue == T_road1 || fourValue == T_road2) {
            if (up != 'X') {
                return 2;
            }
            if (right != 'X') {
                return 21;
            }
            if (down != 'X') {
                return 22;
            }
            if (left != 'X') {
                return 23;
            }

        }
        // 四周是草用十字路
        int cross_road = 352;
        if (fourValue == cross_road) {
            return 3;
        }

        // 上下为路，左右不为路的时候，转path0 90度
        if ((up == 'X' || down == 'X') && left != 'X' && right != 'X') {
            return 101;
        }
        return 0;
    }

    // 将背景图片正确load入
    public void loadBackGround(char[][] backGroundArray) {
        PImage grass = loadImage("src/main/resources/WizardTD/grass.png");
        PImage shrub = loadImage("src/main/resources/WizardTD/shrub.png");

        PImage path0 = loadImage("src/main/resources/WizardTD/path0.png");
        PImage path0_rotate = rotateImageByDegrees(path0, 90);

        PImage path1 = loadImage("src/main/resources/WizardTD/path1.png");
        PImage path1_rotate_1 = rotateImageByDegrees(path1, 90);
        PImage path1_rotate_2 = rotateImageByDegrees(path1, 180);
        PImage path1_rotate_3 = rotateImageByDegrees(path1, 270);

        PImage path2 = loadImage("src/main/resources/WizardTD/path2.png");
        PImage path2_rotate_1 = rotateImageByDegrees(path2, 90);
        PImage path2_rotate_2 = rotateImageByDegrees(path2, 180);
        PImage path2_rotate_3 = rotateImageByDegrees(path2, 270);

        PImage path3 = loadImage("src/main/resources/WizardTD/path3.png");
        wizardImage = loadImage("src/main/resources/WizardTD/wizard_house.png");

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                switch (backGroundArray[i][j]) {
                    case ' ':
                        mapTile[i][j] = grass;
                        break;
                    case 'S':
                        mapTile[i][j] = shrub;
                        break;
                    case 'X':
                        int pathNum = choosePath(i, j, backGroundArray);
                        // int pathNum = choosePath(j, i, backGroundArray);
                        switch (pathNum) {
                            case 0:
                                mapTile[i][j] = path0;
                                break;
                            case 1:
                                mapTile[i][j] = path1;
                                break;
                            case 11:
                                mapTile[i][j] = path1_rotate_1;
                                break;
                            case 12:
                                mapTile[i][j] = path1_rotate_2;
                                break;
                            case 13:
                                mapTile[i][j] = path1_rotate_3;
                                break;
                            case 2:
                                mapTile[i][j] = path2;
                                break;
                            case 21:
                                mapTile[i][j] = path2_rotate_1;
                                break;
                            case 22:
                                mapTile[i][j] = path2_rotate_2;
                                break;
                            case 23:
                                mapTile[i][j] = path2_rotate_3;
                                break;
                            case 3:
                                mapTile[i][j] = path3;
                                break;
                            case 101:
                                mapTile[i][j] = path0_rotate;
                                break;
                        }
                        break;
                    case 'W':
                        // 先把草画在W的地方，存储坐标，在坐标上层画wizard house
                        mapTile[i][j] = loadImage("src/main/resources/WizardTD/grass.png");
                        wizard_house_x = i;
                        wizard_house_y = j;
                        if (backGroundArray[i - 1][j] == 'X') {
                            wizardImage = rotateImageByDegrees(wizardImage, 90);
                            wizard_house_display_x = i * 32 + 40;
                            wizard_house_display_y = j * 32 - 8;
                        }
                        if (backGroundArray[i][j + 1] == 'X') {
                            wizardImage = rotateImageByDegrees(wizardImage, 180);
                            wizard_house_display_x = i * 32 + 40 - 8;
                            wizard_house_display_y = j * 32;
                        }
                        if (backGroundArray[i + 1][j] == 'X') {
                            wizardImage = rotateImageByDegrees(wizardImage, 270);
                            wizard_house_display_x = i * 32 + 40;
                            wizard_house_display_y = j * 32 - 8;
                        }
                        // wizard_house_display_x = i * 32 + 40 - 8;
                        // wizard_house_y = j * 32;
                        break;
                }
            }
        }
    }

    public App() {
        this.configPath = "config.json";

    }

    /**
     * Initialise the setting of the window size.
     */
    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the
     * player, enemies and map elements.
     */

    public static int initial_mana;
    public static int initial_mana_cap;
    public static int initial_mana_gained_per_second;
    public static int current_mana_value; // 这个变量在Topbar里赋值

    private double initial_tower_range;
    private double initial_tower_firing_speed;
    private int initial_tower_damage;
    private int initial_tower_cost;

    public static JSONArray waves;

    private PImage gremlin_image;
    protected PImage tower_0_image;
    protected PImage tower_1_image;
    protected PImage tower_2_image;
    private HashMap<Integer, PImage> tower_image_map = new HashMap<>();

    public static PImage[] gremlin_dead = new PImage[5];

    private ArrayList<MyPoint> findShortPath(HashMap<MyPoint, MyPoint> pair, MyPoint start, MyPoint destination) {
        ArrayList<MyPoint> path = new ArrayList<>();
        MyPoint current = destination;
        // System.out.println("Pair: " + pair.size());
        // System.out.println("Start at: " + start.toString());
        // System.out.println("End at: " + destination.toString());
        while (!current.equals(start)) {
            path.add(0, current);
            // System.out.println(path.size());
            current = pair.get(current);
            // System.out.println(pair.containsKey(current));
        }
        path.add(0, start);
        return path;
    }

    // ------------------------------------BFS
    // start------------------------------------
    private ArrayList<MyPoint> BreadthFirstSearch(MyPoint start, MyPoint destination) {
        // ArrayList<MyPoint> queue = new ArrayList<>();
        LinkedList<MyPoint> queue = new LinkedList<>();
        ArrayList<MyPoint> visited = new ArrayList<>();

        boolean route_found = false;

        // (child, parent)
        HashMap<MyPoint, MyPoint> pair = new HashMap<>();

        queue.addLast(start);
        while (!queue.isEmpty()) {
            MyPoint current = queue.removeFirst();
            visited.add(current);

            if (current.equals(destination)) {
                // MyPoint parent = queue.get(queue.size()-1);
                // pair.put(parent,current);
                // MyPoint lastParent = visited.get(visited.size()-2);
                // pair.put(current,lastParent);

                route_found = true;
                break;
            }

            int x = current.getX();
            int y = current.getY();

            try {
                MyPoint up = new MyPoint(x - 1, y);
                if (Map_Grid[x - 1][y] >= 87 && !visited.contains(up) && !queue.contains(up)) {
                    queue.addLast(up);
                    pair.put(up, current);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                MyPoint down = new MyPoint(x + 1, y);
                if (Map_Grid[x + 1][y] >= 87 && !visited.contains(down) && !queue.contains(down)) {
                    queue.addLast(down);
                    pair.put(down, current);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                MyPoint left = new MyPoint(x, y - 1);
                if (Map_Grid[x][y - 1] >= 87 && !visited.contains(left) && !queue.contains(left)) {
                    queue.addLast(left);
                    pair.put(left, current);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                MyPoint right = new MyPoint(x, y + 1);
                if (Map_Grid[x][y + 1] >= 87 && !visited.contains(right) && !queue.contains(right)) {
                    queue.addLast(right);
                    pair.put(right, current);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }

        }

        // System.out.println(visited.toString());

        if (!route_found) {
            System.out.println("Route not found");
            return null;
        } else {
            System.out.println("Route found");
        }

        return findShortPath(pair, start, destination);
    }
    // ------------------------------------BFS
    // end------------------------------------

    @Override
    public void setup() {
        frameRate(FPS);

        // 读取layout并处理地图板
        JSONObject gameJson = loadJSONObject(configPath);

        // --------------Initialize the map grid START----------
        Map_Grid = readLevel(gameJson.getString("layout"));
        loadBackGround(Map_Grid);
        // --------------Initialize the map grid END----------

        initial_tower_range = gameJson.getDouble("initial_tower_range");
        initial_tower_firing_speed = gameJson.getDouble("initial_tower_firing_speed");
        initial_tower_damage = gameJson.getInt("initial_tower_damage");
        initial_tower_cost = gameJson.getInt("tower_cost");

        initial_mana = gameJson.getInt("initial_mana");
        initial_mana_gained_per_second = gameJson.getInt("initial_mana_gained_per_second");
        initial_mana_cap = gameJson.getInt("initial_mana_cap");

        // 读取wave内容
        waves = gameJson.getJSONArray("waves");

        topbar = new Topbar();
        sidebar = new Sidebar();

        // setup gremlin death animation sequence
        for (int x = 0; x < 5; x++) {
            gremlin_dead[x] = loadImage("src/main/resources/WizardTD/gremlin" + (x + 1) + ".png");
        }

        // 存储最短路径
        MyPoint end = new MyPoint(wizard_house_x, wizard_house_y);
        for (int x = 0; x < startPoints.size(); x++) {
            MyPoint start = startPoints.get(x);
            shortest_Paths.put(start, BreadthFirstSearch(start, end));

            // 如果是左右出生，需要加TOPBAR+上下6
            // 如果上下出生，只需要左右+6
            // monsterList.add(new
            // Gremlin(start.getY()*CELLSIZE,start.getX()*CELLSIZE+TOPBAR+6,true,false));
            System.out.println("Monster adds at: " + (start.getY() * CELLSIZE) + " " + (start.getX() * CELLSIZE));
        }

        for (int x = 0; x < startPoints.size(); x++) {
            System.out.println("shortestPath: " + shortest_Paths.get(startPoints.get(x)).toString());
        }

        System.out.println("startPoints: " + startPoints.toString());

        gremlin_image = this.loadImage("src/main/resources/WizardTD/gremlin.png");

        tower_0_image = this.loadImage("src/main/resources/WizardTD/tower0.png");
        tower_1_image = this.loadImage("src/main/resources/WizardTD/tower1.png");
        tower_2_image = this.loadImage("src/main/resources/WizardTD/tower2.png");
        tower_image_map.put(0, tower_0_image);
        tower_image_map.put(1, tower_1_image);
        tower_image_map.put(2, tower_2_image);

    }

    /**
     * s
     * Receive key pressed signal from the keyboard.
     */
    @Override
    public void keyPressed() {
        // if (key == 'p' || key == 'P') {
        // if (isLooping()) {
        // noLoop();
        // } else {
        // loop();
        // }
        // }
    }

    /**
     * Receive key released signal from the keyboard.
     */
    @Override
    public void keyReleased() {

    }

    public static Button ffButton = new Button("FF", "2x Speed", 650, 50);
    public static Button pauseButton = new Button("P", "PAUSE", 650, 100);
    public static Button buildTowerButton = new Button("T", "Build\nTower", 650, 150);
    public static boolean FF_press_condition = false;
    public static boolean Pause_press_condition = false;
    public static boolean build_tower_condition = false;
    private boolean yes_to_place_tower = false;

    public static String mouse_on_which_button;

    public Tower tower;

    @Override
    public void mousePressed(MouseEvent e) {
        // initial_mana_cap += 250;
        if (ffButton.mouse_on_button(this)) {
            FF_press_condition = !FF_press_condition;
        }

        if (pauseButton.mouse_on_button(this)) {
            Pause_press_condition = !Pause_press_condition;
        }

        if (buildTowerButton.mouse_on_button(this)) {
            build_tower_condition = !build_tower_condition;
        }

        // if(tower.valid_place_tower(this)){
        // yes_to_place_tower = !yes_to_place_tower;
        // }

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /*
     * @Override
     * public void mouseDragged(MouseEvent e) {
     * 
     * }
     */

    /*
     * 提取wave信息
     */
    private double next_wave_pause;

    public static double wave_duration;
    public static double pre_wave_pause;

    private ArrayList<Monster> monsterList = new ArrayList<>();
    private ArrayList<ArrayList<Monster>> big_monsterList = new ArrayList<>();

    public static String monster_type;
    public static double monster_hp;
    public static double monster_speed;
    public static double monster_armour;
    public static int mana_gained_on_kill;
    public static double monster_quantity;
    public double monster_count;

    private double Monster_timer;
    // 更换wave的时机就是duration(startsIn)减为0
    int startTime = millis();
    private int startsIn;

    public int getStartIn() {
        return startsIn;
    }

    private int current_wave = 0;
    private int next_wave_index = current_wave + 1;
    // 这里的next_wave_display别改成static，我在Topbar里面用"App."调用了
    public int next_wave_display = next_wave_index + 1;

    private boolean processWave = true;

    public void wave_change() {
        // int current_wave=0; 从0 wave 开始

        /*
         * 这里顺序不可变，先提取current_wave=0的信息，
         * 然后，next_wave_duration里面才有值，
         * 进而，才能计算startsIn
         * 之后，才能判断是否要change_wave
         */

        // 先提取 current_wave=0 的信息
        if (processWave) {
            process_wave(current_wave);

            this.Monster_timer = (wave_duration / monster_quantity) * FPS;
            // for(int x=0; x<=monster_quantity; x++){
            // monsterList.add(gremlin);
            // }
            monster_count = monster_quantity;
            System.out.println("Monster_timer to draw: "+Monster_timer + "-----\n");
            processWave = false;
        }

        // 计算 startsIn 时间
        int sec = (int) ((millis() - startTime) / 1000.0); // 从 startTime 以来经过的秒数
        startsIn = (int) (next_wave_pause - sec); // 计算倒计时
        if (startsIn <= 0) {
            // 重置 startTime
            startTime = millis();
        }

        // 达到要求，更换wave
        if (current_wave < waves.size() - 1 && startsIn <= 0) {
            current_wave++;
            next_wave_index++;
            next_wave_display++;
            processWave = true;
            // System.out.println(monsterList.size());

        }

    }

    private HashMap<Integer, ArrayList<Object>> monster_info_map = new HashMap<>();

    /**
     * @param cur_wave
     */
    public void process_wave(int cur_wave) {
        JSONObject wave = waves.getJSONObject(cur_wave);
        wave_duration = wave.getInt("duration");
        pre_wave_pause = wave.getDouble("pre_wave_pause");

        // 如果当前wave+1超过size，也就是最后一个wave
        if (cur_wave + 1 >= waves.size()) {
            // TODO 在这里处理最后一个wave的逻辑，不显示wave和startsIn
            next_wave_pause = 5;
        } else {
            // 提取下一波wave的duration
            JSONObject next_wave = waves.getJSONObject(next_wave_index);
            next_wave_pause = next_wave.getInt("pre_wave_pause");
        }

        // 提取monsters信息
        JSONArray monsters = wave.getJSONArray("monsters");
        // monsters.size(): 第三波正常：2
        ArrayList<Object> one_wave_monster = new ArrayList<>();
        for (Integer j = 0; j < monsters.size(); j++) {
            JSONObject monster = monsters.getJSONObject(j);

            monster_type = monster.getString("type");
            monster_hp = monster.getDouble("hp");
            monster_speed = monster.getDouble("speed");
            monster_armour = monster.getDouble("armour");
            mana_gained_on_kill = monster.getInt("mana_gained_on_kill");
            monster_quantity = monster.getInt("quantity");

            one_wave_monster.add(monster_type);
            one_wave_monster.add(monster_hp);
            one_wave_monster.add(monster_speed);
            one_wave_monster.add(monster_armour);
            one_wave_monster.add(mana_gained_on_kill);
            one_wave_monster.add(monster_quantity);
        }
        monster_info_map.put(cur_wave, one_wave_monster);
    }

    // draw the grelin death animation on (x, y) position

    /**
     * Draw all elements in the game by current frame.
     * draw() will be called once every second.
     */
    public static MyPoint spawn_point;

    @Override
    public void draw() {
        background(background_R, background_G, background_B);

        // 如果在setUp设置fill，那么就意味着在整个程序运行后，他都只能执行这个fill

        // 画地图和wizard_house
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                image(mapTile[i][j], j * 32, i * 32 + 40); // 在对应的位置绘制图片
            }
        }

        if (build_tower_condition) {

            tower = new Tower(initial_tower_range, initial_tower_firing_speed, initial_tower_damage,
                    initial_tower_cost);
            tower.setTowerImage(tower_0_image);
            tower.setTowerWithMouse(this);
            // if(yes_to_place_tower){
            // tower.setTowerXY(tower.get_X(),tower.get_Y());
            // }
            // System.out.println(tower.valid_place_tower(this));
            tower.draw(this);
        }

        // 在wave_change()里，每次wave更变，处理monster的更变
        wave_change();

        for (Monster m : monsterList) {
            m.tick();
            m.draw(this);
        }

        this.Monster_timer--;

        // 生成Monster List
        spawn_point = startPoints.get(random.nextInt(startPoints.size()));
        int gremlin_adjust = (CELLSIZE - gremlin_image.width) / 2; // 这里是6
        // TODO 目前开始是从0出生的，还不是地图外

        // 这里这个map是控制wave里monsters里面的信息的
        if (monster_info_map.containsKey(current_wave)) {
            double speed = (double) (monster_info_map.get(current_wave).get(2));
            if (this.Monster_timer < 0 && monster_count != 0) {
                monsterList.add(new Gremlin(spawn_point.getY() * CELLSIZE + gremlin_adjust,
                        spawn_point.getX() * CELLSIZE + TOPBAR + gremlin_adjust, speed, true, false,
                        this.gremlin_image));
                monster_count--;

                this.Monster_timer = (wave_duration / monster_quantity) * FPS;
            }
        }

        image(wizardImage, wizard_house_display_y, wizard_house_display_x);

        topbar.draw(this);
        sidebar.draw(this);

    }

    public static void main(String[] args) {
        PApplet.main("WizardTD.App");
    }

    /**
     * Source:
     * https://stackoverflow.com/questions/37758061/rotate-a-buffered-image-in-java
     * 
     * @param pimg  The image to be rotated
     * @param angle between 0 and 360 degrees
     * @return the new rotated image
     */
    public PImage rotateImageByDegrees(PImage pimg, double angle) {
        BufferedImage img = (BufferedImage) pimg.getNative();
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        PImage result = this.createImage(newWidth, newHeight, ARGB);
        // BufferedImage rotated = new BufferedImage(newWidth, newHeight,
        // BufferedImage.TYPE_INT_ARGB);
        BufferedImage rotated = (BufferedImage) result.getNative();
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                result.set(i, j, rotated.getRGB(i, j));
            }
        }

        return result;
    }
}
