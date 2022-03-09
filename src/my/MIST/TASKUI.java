/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package my.MIST;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.Cursor;
import javax.swing.Timer;
import javax.swing.KeyStroke;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.ActionMap;
import javax.swing.JPanel;
import java.util.Random;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 *
 * @author Joseph Shaffer
 * @version March 20, 2014
 */
public class TASKUI extends javax.swing.JFrame implements ActionListener {
    private final Color ltblu = new Color(100,200,255); //color constant for selection dial (blue)
    private final Color ltgry = new Color(240,240,240); //color constant for selection dial (grey)
    private final RunSettings run;  //data object for storing and accessing run settings
    private Timer time;     //timer for task performance
    private Timer fTime;    //timer for feedback presentation
    private Timer dTime;    //timer for DISDAQ 
    private Timer cTime;    //timer for task block completion
    private Timer bTime;    //timer for the time progress bar
    private final Random rnd = new Random(System.currentTimeMillis());  //random number generator seeded with system time
    private int highlighted = 0;    //variable to store currently selected value on the response dial
    private TaskGen tsk;  //Generator object to create tasks
    int taskCondition = 3;  //variable for storing current task condition (0 = rest, 1 = control, 2 = experimental, 3 = DISDAQ, 4 = End)
    private JPanel panel;   //JPanel object for keyboard input handling
    private int feedbackFlag = 2;   //Flag to indicate whether feedback is being presented (0 = task, 1 = feedback, 2 = DISDAQ)
    private int currentSpeed;   //variable to store current speed
    private int speedCap;       //variable to store maximal speed based on initial settings
    private int avgCounter = 300;   //variable to store fake average performance feedback
    private int usrCounter = 300;   //variable to store user performance feedback
    private int controlCorrect = 0;
    private int controlIncorrect = 0;
    private int expCorrect = 0;
    private int expIncorrect = 0;
    private int controlCorrectCount =0;
    private int easyCount = 2;
    private int medCount = 2;
    private int hardCount = 3;
    private int rights;
    private int errors;
    private int[] order;    //array to store order of task blocks
    private int orderIndex = 0; //index variable for storing progress through task blocks
    private double progress = 0;
    //private int counter = 0;
    private long startTime = 0;
    private BufferedWriter bw;
    private int scannerCount;
    private long pauseTime;
    
    private final String wrongSoundPath = "sounds/wrongAnswerSound.wav";
    private final String rightSoundPath = "sounds/rightAnswerSound.wav";
    private final String timelapseSoundPath = "sounds/timeLapseSound.wav";
    
    private AudioInputStream audioInputStream2;
    private Clip clip2;
    
    
    /**
     * Creates new form TASKUI
     */
 
    public TASKUI(RunSettings run1) {
        initComponents();
        run = run1;
        tsk = new TaskGen(run.getChildrensVersion(),run.getConditionDifficulty());
    }
    
    public void startTask(){
        //Set up key bindings       
        panel = new JPanel();
        this.add(panel);
        InputMap inputMap = panel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(run.getLeftKey().charAt(0)), "Left");
        inputMap.put(KeyStroke.getKeyStroke(run.getRightKey().charAt(0)), "Right");
        inputMap.put(KeyStroke.getKeyStroke(run.getConfirmKey().charAt(0)), "Confirm");
        
         
        ActionMap actionMap = panel.getActionMap();
        actionMap.put("Left", new LeftAction(this));
        actionMap.put("Right", new RightAction(this));
        actionMap.put("Confirm", new ConfirmAction(this));
       
           
        try{
        String path = run.getFilePath() + "\\" + run.getFileName();
        //System.out.println(path);
        File file = new File(path);
        // if file doesnt exists, then create it
	if (!file.exists()) {
			file.createNewFile();
	}
 
	FileWriter fw = new FileWriter(file.getAbsoluteFile());
	bw = new BufferedWriter(fw);
        //bw.write("hi");
        //bw.close();
        } catch (IOException e){
           
        }
        
         //Create array for task run ordering
         order = setOrder();
        
        //Set speed based on control condition speed slider
        switch (run.getControlConditionSpeed()){
            case 1: speedCap = 1666;
                break;
            case 2: speedCap = 2083;
                break;
            case 3: speedCap = 2500;
                break;
            case 4: speedCap = 3333;
                break;
            case 5: speedCap = 4166;
                break;
            case 6: speedCap = 5833;
                break;
            case 7: speedCap = 7500;
                break;
            case 8: speedCap = 12500;
                break;
            default: speedCap = 25000;
                break;
            
        }
        
        //Open audio stream for timer bar sound
        try{
                                          
                        audioInputStream2 = AudioSystem.getAudioInputStream(this.getClass().getResource(timelapseSoundPath));
                        clip2 = AudioSystem.getClip();
                        clip2.open(audioInputStream2);
                        //clip.start( );
        }
        catch(Exception ex){ 
            //System.out.println(ex);
        }
                    
                
        
        
        //Initialize timers
        time = new Timer(4000,this); //Task timer
        fTime = new Timer(2000,this); //Feedback timer
        bTime = new Timer(100,this); //bar timer for animating time bar
        dTime = new Timer(6000,this);
        cTime = new Timer(10000,this);
   
         //Make sure Experimental condition features are hidden
         avgSlider.setVisible(false);
         usrSlider.setVisible(false);
         timeBar.setVisible(false);
         
        //Hide the mouse cursor
        this.setCursor(this.getToolkit().createCustomCursor(
            new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),
            "null"));
                
         //dTime.start();
        
         //If using keyboard key from Scanner
         if(run.getScannerSignal()==1){
            inputMap.put(KeyStroke.getKeyStroke(run.getScannerKey().charAt(0)), "Scanner"); 
            actionMap.put("Scanner", new ScannerAction(this));
            
            if(run.getCountDISDAQs() == 1){
                taskCondition = 3; //Set condition to DISDAQ for incrementScannerCount function
            }
            else {
                nextCondition(); 
            }
         }
         //If using time
         else if(run.getScannerSignal()==2){
             
             if(run.getCountDISDAQs() == 1){
                 dTime.setInitialDelay(run.getDISDAQtime() * 1000); //Set DISDAQ time
                 dTime.start();
             }
             else {
                 nextCondition(); 
             }
         }
         //If using mouse key --NOT IMPLEMENTED
         else if(run.getScannerSignal() == 0){
             
         }
         
        
       
         if(run.getCountDISDAQs() == 1){
             
         }
        
        
        startTime = System.currentTimeMillis();
         //System.out.println("Run Started at " + startTime);
         String out = "Run Started at " + startTime;
         try {
             bw.write(out);
             bw.flush();
         } catch(IOException e){
             
         }
                 
    }
    public int[] setOrder(){
        int[] order;
        int runs = 0;
        int rRuns = 0;
        int cRuns = 0;
        int eRuns = 0;
        
        if(run.getRestEnabled() == 1){
           runs = runs +  run.getRepetitionsWithin();
           //System.out.println("Rest enabled");
        }
        if(run.getControlEnabled() == 1){
           runs = runs +  run.getRepetitionsWithin();
            //System.out.println("Control enabled");
        }
        if(run.getExperimentalEnabled() == 1){
           runs = runs + run.getRepetitionsWithin();
            //System.out.println("Exp enabled");
        }
        //System.out.println("Repetitions = " + run.getRepetitionsWithin());
        //System.out.println("Runs = " + runs);
        order = new int[runs];
        
        int index = 0;
        for (int i = 0; i < run.getRepetitionsWithin(); i++){
            if(run.getRestEnabled()==1){
                order[index] = 0;
                index++;
            }
            if(run.getControlEnabled()==1){
                order[index] = 1;
                index++;
            }
            if(run.getExperimentalEnabled()==1){
                order[index] = 2;
                index++;
            }
            
        }
        
        
       //if order is supposed to be random, we must shuffle
        int adjacent = 1;
        if(run.getOrderOfConditions() ==1){
            
            while(adjacent ==1){
                for (int i = order.length-1; i > 0; i--){
                    int x = rnd.nextInt(i+1);
                    int y = order[x];
                    order[x] = order[i];
                    order[i] = y;
               
                }
                 adjacent = 0;
                if(run.getRestEnabled()==1 && run.getControlEnabled()==1 && run.getExperimentalEnabled()==1){
                   for (int i = 0; i < order.length-1; i++){
                        if (order[i] == order[i+1]){
                            adjacent = 1;
                        }
                    }
                }
               
            }
        }
        
        
        return order;
    }
    
    public void displayProblem(String x){
        problemBox.setText(x);
    }
    public void displayFeedback(String x){
        feedbackBox.setText(x);
    }
  
    @Override
    public void actionPerformed(ActionEvent event){
        //System.out.println(event.getSource().toString());
        //If this is a task timer expiring, set response to timeout, display, start feedback timer
        
        //If the task timer goes off
        if(event.getSource()==time){
            //System.out.println("task timer: " + (Timer)event.getSource());
            time.stop(); // stop the timer
            if (bTime.isRunning()){
                bTime.stop();
            }
            feedbackFlag = 1; //trigger feedback flag
            //displayProblem("");
            confirmResponse(10); //set & display feedback to timeout
            
            pauseTime = System.currentTimeMillis();
            
            
            fTime.start(); //start feedback display timer
            
        }
        //When Feedback timer runs out
        if(event.getSource() == fTime){
            //System.out.println("feedback timer: " + event.getSource());
            fTime.stop(); //stop the timer
         
            nextTask();
           
        }
        //when bar timer runs out
        else if(event.getSource() == bTime){
            double increment = 100/(double)currentSpeed;
            progress = progress + increment;
            int i = (int)(progress * 100) + 2;
            timeBar.setValue(i);
            repaint();
        }
        
        //When DISDAQ timer runs out
        else if(event.getSource() == dTime){
            dTime.stop();
        
            nextCondition();
        }
        //When the condition timer runs out
        else if(event.getSource() == cTime){
            cTime.stop();
           
           nextCondition();
        }
    }
    public void nextTask(){
        String out = "";
        displayFeedback("");
        displayProblem("");
        resetDial();
        feedbackFlag = 0;
        
        //Make sure Experimental condition features are hidden
         avgSlider.setVisible(false);
         usrSlider.setVisible(false);
         timeBar.setVisible(false);
        
        displayProblem(tsk.createTask());
        //System.out.println("Task: " + tsk.getTask());
        //displayProblem("BOB");
        
        if(taskCondition == 1){
           out = "Control Task presented at " + (System.currentTimeMillis()-startTime); 
            currentSpeed = speedCap;
            time.setInitialDelay(currentSpeed);
            
            if(run.getControlTimeout() == 0){
                 time.start(); //restart task timer
            }
        }
        else if(taskCondition == 2){
            out = "Experimental Task presented at " + (System.currentTimeMillis()-startTime);
            
            //Make sure Experimental condition features are hidden
            avgSlider.setVisible(true);
            usrSlider.setVisible(true);
            timeBar.setVisible(true);
            
            //If sound enabled, play sound
                if(run.getSoundEnabled() == 1){
                    //System.out.println("buzz");
                    try{
                          clip2.setFramePosition(0);
                          clip2.start();
                    }
                    catch(Exception ex)
                    { 
                        //System.out.println(ex);
                    }
                    
                }
            
            progress = 0;
            bTime.start();
            
            if (tsk.getDifficulty()==0){
                currentSpeed = 10666/easyCount;
                time.setInitialDelay(currentSpeed);
            }
            else if (tsk.getDifficulty() == 1){
                currentSpeed = 10666/medCount;
                time.setInitialDelay(currentSpeed);
            }
            else if (tsk.getDifficulty() == 2){
                currentSpeed = 10666/hardCount;
                time.setInitialDelay(currentSpeed);
            }
            time.start(); //restart task timer
        }
       
  
         //System.out.println(out);
         try {
            bw.newLine();
            bw.write(out);
            bw.flush();
        } catch(IOException e){
             
        }
    }
    public void nextCondition(){
    
        String out = "";
        int t = 0;
        //Clear text fields
        
        displayFeedback("");
        displayProblem("");
        //reset dial
        resetDial();
        
        
        //Make sure Experimental condition features are hidden
        avgSlider.setVisible(false);
        usrSlider.setVisible(false);
        timeBar.setVisible(false);
        
        
         if (time.isRunning()){
            time.stop();
         }
         if(fTime.isRunning()){
            fTime.stop();
         }
         if (bTime.isRunning()){
            bTime.stop();
         }
           
                 //Stop any sounds
        if(run.getSoundEnabled() == 1){
            //System.out.println("buzz");
            try{
                if(clip2.isActive()){clip2.stop();}
            }
            catch(Exception ex) { 
                //System.out.println(ex);
            }
                    
            }
        
        //Set condition type from order array
        if (orderIndex < order.length){
            taskCondition = order[orderIndex];
            orderIndex++;
        }
        else {
            
     
            JOptionPane.showMessageDialog(null, "Number of tasks: "+ (rights+errors) + "\n" + rights + " correct.\n" + errors + " incorrect.");
            out = "Run Complete";
            taskCondition = 4;
            this.setCursor(Cursor.getDefaultCursor());
            //this.setVisible(false);
            /*MISTUI newwin = new MISTUI();
            newwin.setVisible(true);
            newwin.requestFocus();
            newwin.updateSettings(this.run);*/
        }
        
        //Rest
        if(taskCondition == 0){
            
            //Set resting info for subject
            displayProblem("Please try to keep your head still...");
          //Set output string
         
          out = "Rest Block started at " + (System.currentTimeMillis()-startTime);
          t = run.getRestAcquisitions();
        }
        
        //Control
        else if(taskCondition == 1){
           
            //Set output string
            out = "Control Block started at " + (System.currentTimeMillis()-startTime);
            t = run.getControlAcquisitions();
        }
        
        //Experimental
        else if (taskCondition == 2){
            
            //Set output string
            out = "Experimental Block started at " + (System.currentTimeMillis()-startTime);
            t = run.getExperimentalAcquisitions();
            
            
            
            
            
        }
        
        //Write output to log
         try {
            bw.newLine();
            bw.write(out);
            bw.flush();
          } catch(IOException e){
             
            }    
        //System.out.println(out);
        
        if(taskCondition == 1 || taskCondition == 2){
            nextTask();
        }
        
        //Determine whether we're using time or scanner signal
       if (taskCondition != 4){   
          //If using scanner signal key
          if(run.getScannerSignal() == 1){
           
          }
          //If using time 
          else if(run.getScannerSignal() == 2){
              cTime.setInitialDelay(t*1000);
              cTime.start();
           
          }
          
          //If using mouse click - NOT YET IMPLEMENTED
          else if(run.getScannerSignal() == 0){
              
          }
       }
       
    }
    public void setResponse(int response){
            time.stop();
            feedbackFlag = 1;
            confirmResponse(response);
            
             if (bTime.isRunning()){
                bTime.stop();
            }
            fTime.start();
     
    }
    public void resetDial(){
        highlighted = 0;
        for (int i= 0; i < 10; i++){
            unHighlightResponse(i);
        }
        highlightResponse(highlighted);
    }
    public void confirmResponse(int response){
        
        //Stop any sounds
        if(run.getSoundEnabled() == 1){
            //System.out.println("buzz");
            try{
                if(clip2.isActive()){clip2.stop();}
            }
            catch(Exception ex) { 
                //System.out.println(ex); 
            }
                    
        }
        
        //check if correct
        String feedback = "";
        //System.out.println(response + " " + tsk.getResult());
        if (taskCondition == 1){
            if(response == 10){
                feedback = "\nTimeout\n" + run.getNotRecordedFeedback();
                  if(controlCorrectCount >0){
                    controlCorrectCount--;
                  }
                   errors++;
                  //System.out.println("Control Task Timeout at " + (System.currentTimeMillis()-startTime));
                  String out = "Control Task Timeout at " + (System.currentTimeMillis()-startTime);
                  out = out + " Condition: " + tsk.getCondition();
                  
                    try {
                        bw.newLine();
                        bw.write(out);
                         bw.flush();
                    } catch(IOException e){
             
                    }
            
            }
            else if (response == tsk.getResult())
            {
                feedback = "\n" + run.getCorrectFeedback() + "\n" + run.getNotRecordedFeedback();
                //System.out.println("z");
                controlCorrect++;
                controlCorrectCount++;
                rights++;
                //System.out.println("Control Task Correct at " + (System.currentTimeMillis()-startTime));
           
             String out = "Control Task Correct at " + (System.currentTimeMillis()-startTime);
             out = out + " Condition: " + tsk.getCondition();
                    try {
                        bw.newLine();
                        bw.write(out);
                         bw.flush();
                    } catch(IOException e){
             
                    }
            
            }
            else {
                feedback = "\n" + run.getIncorrectFeedback() + "\n" + run.getNotRecordedFeedback();
                controlIncorrect++;
                errors++;
                if(controlCorrectCount >0){
                    controlCorrectCount--;
                }
                //System.out.println("Control Task Incorrect at " + (System.currentTimeMillis()-startTime));
                
                 String out = "Control Task Incorrect at " + (System.currentTimeMillis()-startTime);
                 out = out + " Condition: " + tsk.getCondition();
                    try {
                        bw.newLine();
                        bw.write(out);
                         bw.flush();
                    } catch(IOException e){
             
                    }
            
            }
        }   
        else if (taskCondition == 2){
            //adjust average performer bar 66% chance of increase, 16.5% chance of no change, 16.5% it decreases
            int x = rnd.nextInt(6);
            if (x==5){
                //no change
            }
            else if (x ==6){
                 //decrease slider
                if (avgCounter >=35){
                    avgCounter-=10;
                }
               
            }
            else {
                //increase slider
                if(avgCounter <=580){
                    avgCounter+=10;
                }
            
            }
          avgSlider.setValue(avgCounter);
            
          if(response == 10){
                feedback = "\nTimeout\n" + run.getRecordedFeedback();
                expIncorrect++;
                errors++;
                if(usrCounter>=35){
                    usrCounter-=10;
                }
                usrSlider.setValue(usrCounter);
                
                //If sound enabled, play sound
                if(run.getSoundEnabled() == 1){
                    //System.out.println("buzz");
                    try{
                                          
                        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(this.getClass().getResource(wrongSoundPath));
                        Clip clip = AudioSystem.getClip();
                        clip.open(audioInputStream);
                        clip.start( );
                    }
                    catch(Exception ex)
                    { 
                       // System.out.println(ex); 
                    }
                    
                }
                
                //System.out.println("Experimental Task Timeout at " + (System.currentTimeMillis()-startTime));
            
                 String out = "Experimental Task Timeout at " + (System.currentTimeMillis()-startTime);
                 out = out + " Difficulty: " + tsk.getDifficulty() + " Hard: " + tsk.getHard() + " Expert: "+ tsk.getExpert() + " Condition: " + tsk.getCondition();
                    try {
                        bw.newLine();
                        bw.write(out);
                         bw.flush();
                    } catch(IOException e){
             
                    }
                
                if ((double)rights/(double)errors < 1){
                    if( tsk.getDifficulty() == 0 && easyCount > 2){
                        easyCount--;
                    }
                    else if (tsk.getDifficulty() == 1 && medCount > 2){
                        medCount--;
                    }
                    else if (tsk.getDifficulty() == 2 && hardCount > 3) {
                        hardCount--;
                    }
                    
                   if (tsk.getDifficulty() == 2 && hardCount== 2){
                        tsk.setHard(0);
                    }
                }
            
                if (tsk.getExpert() == 1 && usrCounter <=250){
                        tsk.setExpert(0);
                    }
                }
                
            else if (response == tsk.getResult()) {
                feedback = "\n" + run.getCorrectFeedback() + "\n" + run.getRecordedFeedback();
                //System.out.println("x");
                rights++;
                expCorrect++;
                if (usrCounter <= 480){
                    usrCounter+=10;
                }
                usrSlider.setValue(usrCounter);
                //System.out.println("Experimental Task Correct at " + (System.currentTimeMillis()-startTime));
           
                 String out = "Experimental Task Correct at " + (System.currentTimeMillis()-startTime);
                out = out + " Difficulty: " + tsk.getDifficulty() + " Hard: " + tsk.getHard() + " Expert: "+ tsk.getExpert() + " Condition: " + tsk.getCondition();
                 try {
                        bw.newLine();
                        bw.write(out);
                         bw.flush();
                    } catch(IOException e){
             
                    }
                
                    if(run.getSoundEnabled() == 1){
                    //System.out.println("buzz");
                    try{
                                          
                        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(this.getClass().getResource(rightSoundPath));
                        Clip clip = AudioSystem.getClip();
                        clip.open(audioInputStream);
                        clip.start( );
                    }
                    catch(Exception ex)
                    { 
                        //System.out.println(ex);
                    }
                    
                }
                    
                if ((double)rights/(double)errors >=1){
                    if( tsk.getDifficulty() == 0 && easyCount < 25){
                        easyCount++;
                    }
                    else if (tsk.getDifficulty() == 1 && medCount < 20){
                        medCount++;
                    }
                    else if (tsk.getDifficulty() == 2 && hardCount < 15) {
                        hardCount++;
                    }
                    
                    if (tsk.getDifficulty() == 1 && medCount== 6){
                        tsk.setHard(1);
                    }
                }
                
                if (easyCount >= 10 || medCount >=20 || hardCount >= 15){
                    if(usrCounter >= 500){
                        tsk.setExpert(1);
                    }
                }
            }
            else {
                feedback = "\n" + run.getIncorrectFeedback() + "\n" + run.getRecordedFeedback();
                errors++;
                expIncorrect++;
                if(usrCounter>=35){
                    usrCounter-=10;
                }
                 usrSlider.setValue(usrCounter);
                //System.out.println("Experimental Task Incorrect at " + (System.currentTimeMillis()-startTime));
           
                 String out = "Experimental Task Incorrect at " + (System.currentTimeMillis()-startTime);
                out = out + " Difficulty: " + tsk.getDifficulty() + " Hard: " + tsk.getHard() + " Expert: "+ tsk.getExpert() + " Condition: " + tsk.getCondition();
                 try {
                        bw.newLine();
                        bw.write(out);
                         bw.flush();
                    } catch(IOException e){
             
                    }
            if(run.getSoundEnabled() == 1){
                    //System.out.println("buzz");
                    try{
                                          
                        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(this.getClass().getResource(wrongSoundPath));
                        Clip clip = AudioSystem.getClip();
                        clip.open(audioInputStream);
                        clip.start( );
                    }
                    catch(Exception ex)
                    { 
                        //System.out.println(ex); 
                    }
                    
                }
                    
            }
        }       
            // record correctness, time, difficulty
            displayFeedback(feedback);
        
        //System.out.println(feedback + " " + tsk.getDifficulty());
        
    }
    public void highlightResponse(int x){
        switch (x){
            case 1: oneBox.setBackground(ltblu);
                break;
            case 2: twoBox.setBackground(ltblu);
                break;
            case 3: threeBox.setBackground(ltblu);
                break;
            case 4: fourBox.setBackground(ltblu);
                break;
            case 5: fiveBox.setBackground(ltblu);
                break;
            case 6: sixBox.setBackground(ltblu);
                break;
            case 7: sevenBox.setBackground(ltblu);
                break;
            case 8: eightBox.setBackground(ltblu);
                break;
            case 9: nineBox.setBackground(ltblu);
                break;
            default: zeroBox.setBackground(ltblu);
                break;
        }
           
    }
     public void unHighlightResponse(int x){
        switch (x){
            case 1: oneBox.setBackground(ltgry);
                break;
            case 2: twoBox.setBackground(ltgry);
                break;
            case 3: threeBox.setBackground(ltgry);
                break;
            case 4: fourBox.setBackground(ltgry);
                break;
            case 5: fiveBox.setBackground(ltgry);
                break;
            case 6: sixBox.setBackground(ltgry);
                break;
            case 7: sevenBox.setBackground(ltgry);
                break;
            case 8: eightBox.setBackground(ltgry);
                break;
            case 9: nineBox.setBackground(ltgry);
                break;
            default: zeroBox.setBackground(ltgry);
                break;
        }
        
        
    }
     public int getHighlighted(){
         return highlighted;
     }
     public void setHightlighted(int x){
         highlighted = x;
     }
     public int getTaskCondition(){
         return taskCondition;
     }
     public int getFeedbackFlag(){
         return feedbackFlag;
     }
     public void incrementScannerSignal(){
         scannerCount++;
         
         if(taskCondition == 0){
             if (scannerCount >= run.getRestAcquisitions()){
                 nextCondition();
                 scannerCount = 0;
             }
         }
         else if (taskCondition == 1){
             if(scannerCount >= run.getControlAcquisitions()){
                 nextCondition();
                 scannerCount = 0;
             }
         }
         else if(taskCondition == 2) {
             if(scannerCount >= run.getExperimentalAcquisitions()){
                 nextCondition();
                 scannerCount = 0;
             }
         }
         else if(taskCondition == 3){
             if(scannerCount >= run.getDISDAQtime()){
                 nextCondition();
                 scannerCount = 0;
             }
         }
         //System.out.println(scannerCount);
     }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        windowPanel = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        problemBox = new javax.swing.JTextField();
        zeroBox = new javax.swing.JTextField();
        oneBox = new javax.swing.JTextField();
        twoBox = new javax.swing.JTextField();
        sixBox = new javax.swing.JTextField();
        threeBox = new javax.swing.JTextField();
        fourBox = new javax.swing.JTextField();
        sevenBox = new javax.swing.JTextField();
        nineBox = new javax.swing.JTextField();
        eightBox = new javax.swing.JTextField();
        fiveBox = new javax.swing.JTextField();
        avgSlider = new javax.swing.JSlider();
        usrSlider = new javax.swing.JSlider();
        jScrollPane2 = new javax.swing.JScrollPane();
        feedbackBox = new javax.swing.JTextPane();
        timeBar = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        windowPanel.setPreferredSize(new java.awt.Dimension(1024, 768));
        windowPanel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                windowPanelKeyTyped(evt);
            }
        });
        windowPanel.setLayout(null);

        jTextField1.setEditable(false);
        jTextField1.setBackground(new java.awt.Color(255, 0, 51));
        jTextField1.setFocusable(false);
        windowPanel.add(jTextField1);
        jTextField1.setBounds(60, 250, 610, 30);

        jTextField2.setEditable(false);
        jTextField2.setBackground(new java.awt.Color(255, 255, 0));
        jTextField2.setFocusable(false);
        windowPanel.add(jTextField2);
        jTextField2.setBounds(660, 250, 190, 30);

        jTextField3.setEditable(false);
        jTextField3.setBackground(new java.awt.Color(0, 204, 0));
        jTextField3.setFocusable(false);
        windowPanel.add(jTextField3);
        jTextField3.setBounds(840, 250, 120, 30);

        problemBox.setEditable(false);
        problemBox.setFont(new java.awt.Font("Tahoma", 0, 48)); // NOI18N
        problemBox.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        problemBox.setFocusable(false);
        windowPanel.add(problemBox);
        problemBox.setBounds(60, 60, 906, 154);

        zeroBox.setEditable(false);
        zeroBox.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        zeroBox.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        zeroBox.setText("0");
        zeroBox.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        windowPanel.add(zeroBox);
        zeroBox.setBounds(769, 470, 35, 32);

        oneBox.setEditable(false);
        oneBox.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        oneBox.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        oneBox.setText("1");
        oneBox.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        windowPanel.add(oneBox);
        oneBox.setBounds(822, 493, 35, 32);

        twoBox.setEditable(false);
        twoBox.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        twoBox.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        twoBox.setText("2");
        twoBox.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        windowPanel.add(twoBox);
        twoBox.setBounds(858, 532, 35, 32);

        sixBox.setEditable(false);
        sixBox.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        sixBox.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        sixBox.setText("6");
        sixBox.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        windowPanel.add(sixBox);
        sixBox.setBounds(716, 623, 35, 32);

        threeBox.setEditable(false);
        threeBox.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        threeBox.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        threeBox.setText("3");
        threeBox.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        windowPanel.add(threeBox);
        threeBox.setBounds(858, 577, 35, 32);

        fourBox.setEditable(false);
        fourBox.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        fourBox.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        fourBox.setText("4");
        fourBox.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        windowPanel.add(fourBox);
        fourBox.setBounds(822, 621, 35, 32);

        sevenBox.setEditable(false);
        sevenBox.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        sevenBox.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        sevenBox.setText("7");
        sevenBox.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        sevenBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sevenBoxActionPerformed(evt);
            }
        });
        windowPanel.add(sevenBox);
        sevenBox.setBounds(687, 578, 35, 32);

        nineBox.setEditable(false);
        nineBox.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        nineBox.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        nineBox.setText("9");
        nineBox.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        windowPanel.add(nineBox);
        nineBox.setBounds(717, 495, 35, 32);

        eightBox.setEditable(false);
        eightBox.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        eightBox.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        eightBox.setText("8");
        eightBox.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        windowPanel.add(eightBox);
        eightBox.setBounds(687, 534, 35, 32);

        fiveBox.setEditable(false);
        fiveBox.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        fiveBox.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        fiveBox.setText("5");
        fiveBox.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        windowPanel.add(fiveBox);
        fiveBox.setBounds(769, 644, 35, 32);

        avgSlider.setMaximum(600);
        avgSlider.setPaintTrack(false);
        avgSlider.setValue(300);
        avgSlider.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        avgSlider.setFocusable(false);
        windowPanel.add(avgSlider);
        avgSlider.setBounds(60, 220, 900, 30);

        usrSlider.setMaximum(600);
        usrSlider.setPaintTrack(false);
        usrSlider.setValue(300);
        usrSlider.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        usrSlider.setFocusable(false);
        windowPanel.add(usrSlider);
        usrSlider.setBounds(60, 280, 900, 26);

        feedbackBox.setEditable(false);
        feedbackBox.setFont(new java.awt.Font("Tahoma", 0, 48)); // NOI18N
        feedbackBox.setFocusable(false);
        feedbackBox.setRequestFocusEnabled(false);
        feedbackBox.setVerifyInputWhenFocusTarget(false);
        jScrollPane2.setViewportView(feedbackBox);

        windowPanel.add(jScrollPane2);
        jScrollPane2.setBounds(60, 350, 450, 300);
        windowPanel.add(timeBar);
        timeBar.setBounds(70, 320, 890, 20);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(windowPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(windowPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setSize(new java.awt.Dimension(1066, 841));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void sevenBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sevenBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sevenBoxActionPerformed

    private void windowPanelKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_windowPanelKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_windowPanelKeyTyped

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
           java.util.logging.Logger.getLogger(TASKUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TASKUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TASKUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TASKUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                //new TASKUI().setVisible(true);
            }
        });
        
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider avgSlider;
    private javax.swing.JTextField eightBox;
    private javax.swing.JTextPane feedbackBox;
    private javax.swing.JTextField fiveBox;
    private javax.swing.JTextField fourBox;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField nineBox;
    private javax.swing.JTextField oneBox;
    private javax.swing.JTextField problemBox;
    private javax.swing.JTextField sevenBox;
    private javax.swing.JTextField sixBox;
    private javax.swing.JTextField threeBox;
    private javax.swing.JProgressBar timeBar;
    private javax.swing.JTextField twoBox;
    private javax.swing.JSlider usrSlider;
    private javax.swing.JPanel windowPanel;
    private javax.swing.JTextField zeroBox;
    // End of variables declaration//GEN-END:variables
}

class LeftAction extends AbstractAction {
    
    private TASKUI x;
    public LeftAction (TASKUI a){
        x = a;
    }
    
    @Override
    public void actionPerformed(ActionEvent actionEvent){
        if(x.getTaskCondition() != 0){
            if(x.getFeedbackFlag() == 0){
                //System.out.println("Left");
                int y = x.getHighlighted();
                x.unHighlightResponse(y);
                if (y > 0){
                    y--;
                }
                else {
                    y = 9;
                }
                x.highlightResponse(y);
                x.setHightlighted(y);
            }
        }
    }
}

class RightAction extends AbstractAction {
    
    private TASKUI x;
    public RightAction (TASKUI a){
        x = a;
    }
    
    @Override
    public void actionPerformed(ActionEvent actionEvent){
        if(x.getTaskCondition() != 0){
            if(x.getFeedbackFlag()==0){
                //System.out.println("Right");
                int y = x.getHighlighted();
                x.unHighlightResponse(y);
                if (y < 9){
                    y++;
                }
                else {
                    y = 0;
                }
                x.highlightResponse(y);
                x.setHightlighted(y);
            }
        }
    }
}
class ConfirmAction extends AbstractAction {
    
    private TASKUI x;
    public ConfirmAction (TASKUI a){
        x = a;
    }
    
    @Override
    public void actionPerformed(ActionEvent actionEvent){
        if(x.getTaskCondition() != 0){
            if(x.getFeedbackFlag() == 0){
                //System.out.println("Confirm" + x.getHighlighted());
                x.setResponse(x.getHighlighted());
                //x.unHighlightResponse(x.getHighlighted());
            }
        }
    }
}

class ScannerAction extends AbstractAction {
    
    private TASKUI x;
    public ScannerAction (TASKUI a){
        x = a;
    }
    
    @Override
    public void actionPerformed(ActionEvent actionEvent){
      x.incrementScannerSignal();
    }
}