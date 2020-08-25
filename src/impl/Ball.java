package impl;


import behavior.behaviors.Collidable;
import behavior.behaviors.KeyBehavior;
import game.Element;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

public class Ball extends Element implements Collidable, KeyBehavior {

    private double deltaY;
    private double deltaX;
    private double ballSpeed;
    private int lives;
    public double directionDegrees;
    
    ControllerManager controllers = new ControllerManager();
    

    public Ball() {
        super("/resources/ball.png");
        this.deltaY = 0;
        this.deltaX = 0;
        this.lives = 1;
        this.autosize();
        this.resetBall();
        Random r = new Random();
        int low = 190;
        int high = 350;
        int result = r.nextInt(high-low) + low;
        directionDegrees = result;
        controllers.initSDLGamepad();
    }
    

    public void resetBall() {
    	ballSpeed = 0;
    	this.setX(450);
        this.setY(835);
    }

    @Override
    public void handleKeyPresses(ArrayList<String> arrayList) {
        this.deltaX = 0;
        this.deltaY = 0;
        
        
        //ball richting is bepaald door directionDegrees
        double directionRadians = Math.toRadians(this.directionDegrees);
        double directionX = Math.cos(directionRadians) * ballSpeed;
        double directionY = Math.sin(directionRadians) * ballSpeed;        
        
        
        super.setX(super.getX()+directionX);
        this.deltaX = directionX;
        
        super.setY(super.getY()+directionY);
        this.deltaY = directionY;
        
        //reverse ball en speel geluid
        if (this.getX() > 940 || this.getX() < 60) {
        	this.reverseBall("vertical");
        	playSound("border");
        }
        
        if (this.getY() < 60 || this.getY() > 940) {
        	this.reverseBall("horizontal");
        	playSound("border");
        }
        
        ControllerState currState = controllers.getState(0);
        if(currState.b || arrayList.contains("SPACE")) {
        	if (ballSpeed == 0) {
        		ballSpeed = 5;
        	}
        }
        
    }
    
    public void playSound(String soundType) {
    	if (Runner.soundOn == true) {
	    	String musicborder = System.getProperty("user.dir") + "\\src\\resources\\" + soundType + ".wav";
		    Media sound = new Media(new File(musicborder).toURI().toString());
		    MediaPlayer mediaPlayer = new MediaPlayer(sound);
		    mediaPlayer.play();
    	}
    }
    
    @Override
    public void handleCollision(Collidable collidable) {
    	if(collidable instanceof BreakBlockRed){
    		ballSpeed *= 1.002;
    		if (ballSpeed > 15) {
    			ballSpeed = 15;
    		}
  
    		// check of breakblock wel bestaat met if statement
    		if (((BreakBlockRed) collidable).active == true) {
    			//speel break geluid
    			playSound("bump");
    			
    			//draai de ball
    			reverseBall("horizontal");
    			
    			//zet het blokje uit
    			((BreakBlockRed) collidable).active = false;
    			((BreakBlockRed) collidable).setVisible(false);
    			Scoreboard.plusScore();
    		}
	    }
    	
    	if(collidable instanceof PlankjeL){
    		this.reverseBall("left");
	    }
    	if(collidable instanceof PlankjeR){
    		this.reverseBall("right");
	    }
    	if(collidable instanceof PlankjeM){
    		this.reverseBall("middle");
	    }
    	if(collidable instanceof PlankjeLM){
    		this.reverseBall("lmiddle");
	    }
    	if(collidable instanceof PlankjeRM){
    		this.reverseBall("rmiddle");
	    }
    	
    	if(collidable instanceof Lava){
    		resetBall();
    		this.lives -= 1;
    		if (lives <= 0) {
    			//game over!
    			Alert alert = new Alert(AlertType.INFORMATION);
        		alert.setTitle("You dead!");
        		alert.setHeaderText(null);
        		alert.setContentText("Nice try. Score: " + Scoreboard.getScore());
        		alert.show();
        		try {
					User.setScore(Scoreboard.getScore(), User.getUsername());
				} catch (Exception e) {
					e.printStackTrace();
				}
        		/* try {
					TimeUnit.SECONDS.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} */
        		System.exit(0);
    		}
	    }
    }

    public void reverseBall(String direction) {
    	double directionDegreesNew = 0;
    	switch(direction) {
    	case "left":
    		directionDegreesNew = directionDegrees = Math.random()*10 + 200;
    		break;
    	case "right":
    		directionDegreesNew = directionDegrees = Math.random()*10 + 330;
    		break;
    	case "middle":
    		directionDegreesNew = directionDegrees = Math.random()*10 + 265;
    		break;
    	case "lmiddle":
    		directionDegreesNew = directionDegrees = Math.random()*10 + 240;
    		break;
    	case "rmiddle":
    		directionDegreesNew = directionDegrees = Math.random()*10 + 290;
    		break;
    	case "horizontal":
    		directionDegreesNew = directionDegrees -= (directionDegrees-180)*2;
    		break;
    	case "vertical":
    		directionDegreesNew = directionDegrees -= (directionDegrees-270)*2;
    		break;
    	}	
    	
        this.directionDegrees = directionDegreesNew;
    }
    
    public double getDeltaY() {
        return deltaY;
    }

    public void setDeltaY(double deltaY) {
        this.deltaY = deltaY;
    }

    public double getDeltaX() {
        return deltaX;
    }

    public void setDeltaX(double deltaX) {
        this.deltaX = deltaX;
    }
}
