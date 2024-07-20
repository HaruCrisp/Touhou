import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;



public class Touhous extends JPanel implements ActionListener, KeyListener {
    //board
    int tileSize = 32;
    int rows = 16;
    int columns = 16;
    int boardWidth = tileSize * columns; //32*16
    int boardHeight = tileSize * rows;

    Image mainImg;
    Image waifu1Img;
    Image waifu2Img;
    Image waifu3Img;
    Image waifu4Img;
    ArrayList<Image> waifuImgArray;

    class Block{
        int x;
        int y;
        int width;
        int height;
        Image img;
        boolean alive = true; //waifus
        boolean used = false; //bullets

        Block(int x, int y, int width, int height, Image img){
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    //main waifu thickness
    int mainWidth = tileSize;//32px
    int mainHeight = tileSize*2; //62px
    int mainX = tileSize*columns/2 - tileSize;
    int mainY = boardHeight - tileSize*3;
    int mainVelocityX = tileSize; //moving speed
    Block main;

    ArrayList<Block> waifuArray;
    int waifuWidth = tileSize;
    int waifuHeight = tileSize;
    int waifuX = tileSize;
    int waifuY = tileSize;

    int waifuRows = 4;
    int waifuColumns = 6;
    int waifuCount = 0;
    int waifuVelocityX = 4; //waifu dash spid

    //bullets
    ArrayList<Block>bulletArray;
    int bulletWidth = tileSize/4;
    int bulletHeight = tileSize/4;
    int bulletVelocityY = -10; //bullet travel speed

    Timer gameLoop;
    int score = 0;
    boolean gameOver = false;

    Touhous(){
        setPreferredSize(new Dimension (boardWidth,boardHeight));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);

        //load images
        mainImg = new ImageIcon(getClass().getResource("./main.png")).getImage();
        waifu1Img = new ImageIcon(getClass().getResource("./waifu1.png")).getImage();
        waifu2Img = new ImageIcon(getClass().getResource("./waifu2.png")).getImage();
        waifu3Img = new ImageIcon(getClass().getResource("./waifu3.png")).getImage();
        waifu4Img = new ImageIcon(getClass().getResource("./waifu4.png")).getImage();

        waifuImgArray = new ArrayList<Image>();
        waifuImgArray.add(waifu1Img);
        waifuImgArray.add(waifu2Img);
        waifuImgArray.add(waifu3Img);
        waifuImgArray.add(waifu4Img);

        main = new Block(mainX,mainY,mainWidth,mainHeight,mainImg);
        waifuArray = new ArrayList<Block>();
        bulletArray = new ArrayList<Block>();

        //timer
        gameLoop = new Timer(1000/60, this); //16.7
        createWaifus();
        gameLoop.start();
    }    
    
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    
    public void draw(Graphics g){
        g.drawImage(main.img, main.x, main.y, main.width,main.height,null);

        for (int i=0; i < waifuArray.size(); i++){
            Block waifu = waifuArray.get(i);
            if (waifu.alive){
                g.drawImage(waifu.img, waifu.x, waifu.y, waifu.width, waifu.height, null);
            }
        }

        //bullets
        g.setColor(Color.red);
        for (int i = 0; i < bulletArray.size(); i++){
            Block bullet = bulletArray.get(i);
            if(!bullet.used){
                g.drawRect(bullet.x, bullet.y, bulletWidth, bulletHeight);
            }
        }

        //score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if(gameOver){
            g.drawString("Dej. . . " + String.valueOf(score), 10, 35);
        }
        else {
            g.drawString(String.valueOf(score), 10, 35);
        }
    }

    //waifus
    public void move() {
        //waifus
        for (int i = 0; i < waifuArray.size(); i++){
            Block waifu = waifuArray.get(i);
            if(waifu.alive){
                waifu.x += waifuVelocityX;

                //if waifu army touches wall
                if (waifu.x + waifu.width >= boardWidth || waifu.x <= 0){
                    waifuVelocityX *= -1;
                    waifu.x += waifuVelocityX*2;

                    //waifu down 1 row
                    for (int j = 0; j < waifuArray.size(); j++){
                        waifuArray.get(j).y += waifuHeight;
                    }
                }
                if (waifu.y >= main.y){
                    gameOver = true;
                }
            }
        }
        //bullet
        for (int i = 0; i < bulletArray.size(); i++){
            Block bullet = bulletArray.get(i);
            bullet.y += bulletVelocityY;

            //bullet collision
            for (int j = 0; j < waifuArray.size(); j++){
                Block waifu = waifuArray.get(j);
                if(!bullet.used && waifu.alive && detectCollision(bullet, waifu)){
                    bullet.used = true;
                    waifu.alive = false; //dej
                    waifuCount--;
                    score += 100;
                }
            }
        }
        while (bulletArray.size() > 0 && (bulletArray.get(0).used || bulletArray.get(0).y < 0)){
            bulletArray.remove(0);
        } 

        //respawn
        if(waifuCount == 0) {
            //increase the number of waifus in columns and rows by 1
            score += waifuColumns * waifuRows * 100;
            waifuColumns = Math.min(waifuColumns + 1, columns/2 -2);
            waifuRows = Math.min(waifuRows + 1, rows - 6);
            waifuArray.clear();
            bulletArray.clear();
            createWaifus();
        }
    }

    public void createWaifus(){
        Random random = new Random();
        for (int r = 0;r < waifuRows; r++){
            for (int c = 0; c < waifuColumns; c++){
                int randomImgIndex = random.nextInt(waifuImgArray.size());
                Block waifu = new Block(
                    waifuX + c*waifuWidth,
                    waifuY + r*waifuHeight,
                    waifuWidth,
                    waifuHeight,
                    waifuImgArray.get(randomImgIndex)
                );
                waifuArray.add(waifu);
            }
        } 
        waifuCount = waifuArray.size();
    }

    //collision detection
    public boolean detectCollision(Block a, Block b){
        return a.x < b.x + b.width &&
        a.x + a.width > b.x &&
        a.y < b.y + b.height &&
        a.y + a.height > b.y;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
       move();
       repaint();
       if(gameOver){
        gameLoop.stop();
       }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    //movement
    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver){
            main.x = mainX;
            waifuArray.clear();
            bulletArray.clear();
            score = 0;
            waifuVelocityX = 4;
            waifuColumns = 4;
            waifuColumns = 6;
            gameOver = false;
            createWaifus();
            gameLoop.start();
        }

        else if (e.getKeyCode() == KeyEvent.VK_LEFT && main.x - mainVelocityX >= 0){
            main.x -= mainVelocityX; //left dash dance

        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT && main.x + main.width + mainVelocityX <= boardWidth){
            main.x += mainVelocityX; //right dash dance
        }

        else if (e.getKeyCode() == KeyEvent.VK_SPACE){
            Block bullet = new Block(main.x + mainWidth*12/32, main.y, bulletWidth, bulletHeight, null);
            bulletArray.add(bullet);
        }
    }
}
