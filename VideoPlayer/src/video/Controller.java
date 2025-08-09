package video;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Controller implements Initializable {

	private File f;
	private Media m;
	private MediaPlayer mp;
	private MediaPlayer mp2;
	private MediaPlayer mp3;
	
	

	@FXML
	private MenuBar menubar;
	
	@FXML
	private Pane alt;
	
	@FXML
	private Button baslat;

	@FXML
	private Button ileri;
	
	@FXML
	private Button geri;
	
	@FXML
	private AnchorPane player;
	
	@FXML
	private MediaView mv;
	
	@FXML
	private Slider kaydir;
	
	@FXML
	private TextField sure;
	
	@FXML
	private MenuItem video;
	
	private Duration mpTime;

	private boolean mute = false;
	
	boolean sb2x = false;
	
	boolean sb3x = false;
	
	boolean checkAlt = false;
	
	boolean checkBaslat = false;
	
	private boolean animationWorking = false;
	
	private AtomicBoolean isDragging = new AtomicBoolean(false);
	
	private String path = new String("src/stock.mp4");
	
	
	//Setupping media
	
	// File to media, media to media player, mediaplayer to mediaview
	public void setupMedia() {
		
		
		f = new File(path);
		m = new Media(f.toURI().toString());
		
		mp = new MediaPlayer(m);
		mp2 = new MediaPlayer(m);
		mp3 = new MediaPlayer(m);
		
		mv.setMediaPlayer(mp);
		
		mp.stop();
		mp2.stop();
		mp3.stop();
		
		checkBaslat = false;
		
		
	}
	
	//Images for the media controls
	public void mediaControls() {
		
		Image img = new Image("play_white.png");
		Image img2 = new Image("forward_black.png");
		Image img3 = new Image("backward_black.png");
		
		ImageView imgw = new ImageView(img);
		ImageView imgw2 = new ImageView(img2);
		ImageView imgw3 = new ImageView(img3);
		
		
		imgw.setFitWidth(60);
		imgw.setFitHeight(60);
		
		imgw2.setFitWidth(20);
		imgw2.setFitHeight(20);
		
		imgw3.setFitWidth(20);
		imgw3.setFitHeight(20);
		
		baslat.setGraphic(imgw);
		ileri.setGraphic(imgw2);
		geri.setGraphic(imgw3);
	
		
	}
	
	
	//Location binding for forward, backward and duration nodes
	public void mediaBinding() {
		
		
		
		baslat.layoutXProperty().bind(
				
				alt.widthProperty().subtract(baslat.widthProperty()).divide(2)
				
				);
		
		baslat.layoutYProperty().bind(
				
				alt.heightProperty().subtract(baslat.heightProperty()).divide(2)
				
				);
		
		ileri.layoutXProperty().bind(
				
				baslat.layoutXProperty().add(baslat.widthProperty().add(40))
				
				);
		
		ileri.layoutYProperty().bind(
				
				alt.heightProperty().subtract(ileri.heightProperty()).divide(2)
				
				);
		
		geri.layoutXProperty().bind(
		        
				baslat.layoutXProperty().subtract(geri.widthProperty()).subtract(40)
		    
				);
		  
		geri.layoutYProperty().bind(
		      
				alt.heightProperty().subtract(geri.heightProperty()).divide(2)
		   
				);
		
		sure.layoutYProperty().bind(
				
				alt.heightProperty().subtract(sure.heightProperty()).divide(2)
				
				);

	}
	
	public void listeners() {
		
		//Dynamic positioning for the pane depends on the player
		player.widthProperty().addListener((_,_,newVal) -> {
			
			double newW = newVal.doubleValue();
			alt.setMinWidth(newW);
			
			
		});
		
		player.widthProperty().addListener((_,_,newVal) -> {
			
			double newW = newVal.doubleValue();
			kaydir.setMinWidth(newW);
			
		});
		

		
		//Video width tracking
		player.widthProperty().addListener((_,_,newVal) -> {
		    
			double newW = newVal.doubleValue();
		    double mvW = mp.getMedia().getWidth();
		    mv.setLayoutX((newW - mvW) / 2);
		    
		});
		
		//Video height tracking
		player.heightProperty().addListener((_,_,newVal) ->{
		
			double menuH = menubar.getHeight();
			double newH = newVal.doubleValue();
			double mvH = mp.getMedia().getHeight();
			mv.setLayoutY((newH-mvH-menuH)/2);
			
			
		});
		

		double videoWidth = mp.getMedia().getWidth();
		double videoHeight = mp.getMedia().getHeight();
		
		Stage s = (Stage) mv.getScene().getWindow(); //mv üzerinden bütün stage'i alıyorum. Başka yöntemde kullanılabilir tabi scene üzerinden falan da çekilebilir.
		
		if (s!= null) {
			
			mv.getScene().getWindow().setWidth(videoWidth);
			mv.getScene().getWindow().setHeight(videoHeight);
			
			s.setMinWidth(900);
			s.setMinHeight(900);
			
		}
		
		//Managing slider's length and position with arranging duration
		mp.currentTimeProperty().addListener((_,_,newVal) -> {
			
			if (isDragging.get() == false) {
				
				Duration duration = mp.getTotalDuration();
				kaydir.setMax(duration.toSeconds());
				
				kaydir.setValue(newVal.toSeconds());
				
				int currenttime = (int)newVal.toSeconds();
				
				int totaltime = (int)duration.toSeconds();
				
				String currentS = String.format("%02d:%02d", currenttime / 60, currenttime % 60 );
				String totalS = String.format("%02d:%02d", totaltime / 60, totaltime % 60);
				
				sure.setText(currentS + "/" + totalS);
				
				
				
			}
			
			
		});
		
	}
	
	//Slider Drag 
	public void sliderDrag() {
		
		kaydir.setOnMousePressed(_ -> isDragging.set(true));
		kaydir.setOnMouseReleased(_ -> {
			

			isDragging.set(false);
			
			if (sb2x == true) {
				
				mp.pause();
				mp2.pause();
				
				mp.seek(Duration.seconds(kaydir.getValue()));
				mp2.seek(Duration.seconds(kaydir.getValue()));
				
				checkBaslat = false;
				
				
			}
			else if (sb3x == true) {
				
				mp.pause();
				mp2.pause();
				
				mp.seek(Duration.seconds(kaydir.getValue()));
				mp2.seek(Duration.seconds(kaydir.getValue()));
				mp3.seek(Duration.seconds(kaydir.getValue()));
				
				checkBaslat = false;
				
				
			}
			
			else {
				mp.seek(Duration.seconds(kaydir.getValue()));
				
				checkBaslat = false;
			}
			
		});
		
		
	}
	
	//Bottom pane fadeOut animation
	private void fadeOutPane(Pane pane) {
	    animationWorking = true;
	    FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), pane);
	    fadeOut.setFromValue(1.0);
	    fadeOut.setToValue(0.0);
	    fadeOut.setOnFinished(_ -> {
	        pane.setVisible(false);
	        animationWorking = false;
	    });
	    fadeOut.play();
	}
	
	//Bottom pane fadeIn animation
	private void fadeInPane(Pane pane) {
		animationWorking = true;
	    pane.setVisible(true);
	    FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), pane);
	    fadeIn.setFromValue(0.0);
	    fadeIn.setToValue(1.0);
	    fadeIn.setOnFinished(_ -> animationWorking = false);
	    fadeIn.play();
	}
	
	//Trigger animation when clicking on the screen
	public void focus() {

		//Doesn't work if animation is already working, bug fix
		if (animationWorking) return;
		if (checkAlt == false) {
			
			fadeInPane(alt);
			checkAlt = true;
			
		}
		
		else {
			
			fadeOutPane(alt);
			checkAlt = false;
		}
		
	}
	
	//Opening new video file
	public void videoNew() {
		
		ExtensionFilter ef = new ExtensionFilter("Video Files", "*.mp4");
		//ExtensionFilter ef2 = new ExtensionFilter("All Files", "*.*");
		
		Stage s = (Stage) mv.getScene().getWindow(); //Getting stage from another node
		
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().addAll(ef);
		File sfile = fc.showOpenDialog(s);
		
		if (sfile != null) {
			
			path = sfile.getPath();
			setupMedia();
			initialize(null, null);
			
		}
		
	};
	
	
	//Play button settings
	public void play() {
			
	 if (checkBaslat == false) {
		
		 if (sb3x == true) {
				Platform.runLater(() -> {
					
					mp.play();
					mp2.play();
					mp3.play();
					checkBaslat = true;
				});
			}
		 
		 else if (sb2x == true) {
				Platform.runLater(() -> {
			    		
				    mp.play();
				    mp2.play();
				    checkBaslat = true;
			
				});
			}
			
				
		 else {
			 Platform.runLater(() -> {
		    		
				    mp.play();
				    checkBaslat = true;
				    
				});
				
				}
			}
	 
	 else {
		 mp.pause();
		 mp2.pause();
		 mp3.pause();
				
		 mpTime = mp.getCurrentTime();
				
				
		 mp.seek(mpTime);
		 mp2.seek(mpTime);
		 mp3.seek(mpTime);
		 checkBaslat = false;

	}
		 
	}
	
	//Slider press
	public void sliderPressed(){
		
		if (sb2x == true) {
			
			
			mp.pause();
			mp2.pause();
			
			mp.seek(Duration.seconds(kaydir.getValue()));
			mp2.seek(Duration.seconds(kaydir.getValue()));
			
			checkBaslat = false;
		
			
		}
		
		else if (sb3x == true) {
			
			
			mp.pause();
			mp2.pause();
			mp3.pause();
			
			mp.seek(Duration.seconds(kaydir.getValue()));
			mp2.seek(Duration.seconds(kaydir.getValue()));
			mp3.seek(Duration.seconds(kaydir.getValue()));
			
			checkBaslat = false;
			
			
		}
		
		else {
			
			mp.seek(Duration.seconds(kaydir.getValue()));
			
		}
		
		
		
	}
	
	//Forward button settings
	public void forward() {
		
		mpTime = mp.getCurrentTime();
		
		Duration target = mpTime.add(Duration.seconds(10));
		
		Duration total = mp.getTotalDuration();
		
		if (target.lessThan(total)) {
			
			
			if (sb2x == true) {
				
				mp.pause();
				mp2.pause();
				
				mp.seek(target);
				mp2.seek(target);
				
				checkBaslat = false;
			
				
			}
			
			else if (sb3x == true) {
				
				mp.pause();
				mp2.pause();
				mp3.pause();
				
				
				mp.seek(target);
				mp2.seek(target);
				mp3.seek(target);
				
				checkBaslat = false;
				
				
			}
			
			else {
				
				mp.pause();
				
				mp.seek(target);
				
				checkBaslat = false;
				
			}
			
			
		}
		
	}
	
	//Backward button settings
	public void backward() {
		
		mpTime = mp.getCurrentTime();
		
		Duration target = mpTime.subtract(Duration.seconds(10));
		
		if (target.greaterThan(Duration.ZERO)) {
			
			if (sb2x == true) {
				
				mp.pause();
				mp2.pause();
				
				
				mp.seek(target);
				mp2.seek(target);
				
				checkBaslat = false;
				
			}
			
			else if (sb3x == true) {
				
				mp.pause();
				mp2.pause();
				mp3.pause();
				
				mp.seek(target);
				mp2.seek(target);
				mp3.seek(target);
				
				checkBaslat = false;
				
			}
			
			else {
				
				mp.pause();
				
				mp.seek(target);
				
				checkBaslat = false;
			}
			
		}
		
		else {
		
			
			mp.seek(Duration.ZERO);
			mp2.seek(Duration.ZERO);
			mp3.seek(Duration.ZERO);
			
			
		}
		
		
	}
	
	
	//First working method using fxml file
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		setupMedia();
		
		mediaControls();
		
		mp.setOnReady(() -> {;
		
		listeners();
		
		mediaBinding();
		
		});
		
		sliderDrag();
		
	}
	
	
	//--------------SOUND SETTINGS FROM HERE ON-------------//
	
    //Mute
    public void mute() {
		
    	if (!mute) {
    		mp.setMute(true);
    		mp2.setMute(true);
    		mp3.setMute(true);
        	
    		System.out.println("Susturuldu");
        	mute = true;
		}
    	
    	else {
			mp.setMute(false);
			mp2.setMute(false);
			mp3.setMute(false);
			
			System.out.println("Ses Açıldı");
			mute = false;
		}
    	
	}
    
    //%25 sound
    public void per25() {
    	
    	if (mute) {
			
    		mp.setMute(false);
    		mp2.setMute(false);
    		mp3.setMute(false);
    		
    		mp.setVolume(0.25);
    		mp2.setVolume(0.25);
    		mp3.setVolume(0.25);
    		
    		mute = false;    		
    		
		}
    	
    	else {
    		mp.setVolume(0.25);
    		mp2.setVolume(0.25);
    		mp3.setVolume(0.25);
		}
    	
    }

    //%50 sound
    public void per50() {
    	
    	if (mute) {
			
    		mp.setMute(false);
    		mp2.setMute(false);
    		mp3.setMute(false);
    		
    		mp.setVolume(0.5);
    		mp2.setVolume(0.5);
    		mp3.setVolume(0.5);
    		
    		mute = false;
    		
		}
    	
    	else {
    		mp.setVolume(0.5);
    		mp2.setVolume(0.5);
    		mp3.setVolume(0.5);
		}
    	
    	
    }
    
    //%75 sound
    public void per75() {
    	
    	if (mute) {
			
    		mp.setMute(false);
    		mp2.setMute(false);
    		mp3.setMute(false);
    		
    		mp.setVolume(0.75);
    		mp2.setVolume(0.75);
    		mp3.setVolume(0.75);
    		
    		mute = false;
		}
    	
    	else {
    		mp.setVolume(0.75);
    		mp2.setVolume(0.75);
    		mp3.setVolume(0.75);
		}
    	
    }
    
    //Full sound
    public void per100() {
    	
    	if (mute) {
			
    		mp.setMute(false);
    		mp2.setMute(false);
    		mp3.setMute(false);
    		
    		mp.setVolume(1);
    		mp2.setVolume(1);
    		mp3.setVolume(1);
    		
    		mute = false;
    		
		}
    	
    	else {
    		mp.setVolume(1);
    		mp2.setVolume(1);
    		mp3.setVolume(1);
		}
    	
    }
    
    //--------------SOUND BOOST SETTINGS FROM HERE ON-------------// /!BETA/
    
    //Resetting boost
    public void sb1x() {
		
    	sb2x = false;
    	sb3x = false;
    	
    	mp2.pause();
    	mp3.pause();
    	
    	
	}
    
    //2x
    public void sb2x() {
    	
    	if (sb2x == false) {
			
    		
    		mp.pause();
    		mp2.pause();
    		mp3.pause();
    		
    		mpTime = mp.getCurrentTime();
    		
    		
    		//Seek sonrası thread sleep vermek en iyisi, diğer hiçbir yöntem çalışmadı 
    		mp.seek(mpTime);
        	mp2.seek(mpTime);
        	
        	
        	//Bu senkronizasyonu yapana kadar grok mu kalmadı ben mi kalmadım chatgpt mi kalmadı amk.
        	new Thread(() -> {
                try {
                    Thread.sleep(50); // küçük bir delay tüm işlemler için buffer yaratır. AMK BUNUN
                    Platform.runLater(() -> {
                        mp.play();
                        mp2.play();
                        checkBaslat = true;
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        	
        	sb3x = false;
    		sb2x = true;
    		
		}
    }
    
    //3x
    public void sb3x() {
    	
    	if (sb3x == false) {
		
    		
    		mp.pause();
    		mp2.pause();
    		mp3.pause();
    		
    		mpTime = mp.getCurrentTime();
    		
    		
    		mp.seek(mpTime);
    		mp2.seek(mpTime);
    		mp3.seek(mpTime);
    		
    		
    		new Thread(() -> {
    			
    			try {
					
    				Thread.sleep(30);
    				Platform.runLater(() -> {
    					
    					mp.play();
        				mp2.play();
    					mp3.play();
    					checkBaslat = true;
    					
    				});
    				
    				
				} catch (Exception e) {
					e.printStackTrace();
				}
    			
    		}).start();
    		
    		sb2x = false;
    		sb3x = true;
    		
		}
    		
    }
   
}