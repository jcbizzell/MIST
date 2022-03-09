/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package my.MIST;

import java.util.StringTokenizer;

/**
 *
 * @author Joseph Shaffer
 * @version March 20, 2014
 */
public class RunSettings {
private int restEnabled = 1;
private int controlEnabled = 1;
private int experimentalEnabled = 1;
private int restAcquisitions = 2;
private int controlAcquisitions = 2;
private int experimentalAcquisitions = 2;
private int controlConditionSpeed = 40;
private int orderOfConditions = 1;
private int repetitionsWithin = 3;
private String subjID = "001";
private String filePath = ".";
private String fileName = "001";
private int scannerSignal = 0;
private String scannerKey = "a";
private String leftKey = "1";
private String rightKey = "3";
private String confirmKey = "2";
private int controlTimeout = 1;
private int nBackNum = 0;
private String correctFeedback = "CORRECT";
private String incorrectFeedback = "INCORRECT";
private String recordedFeedback = "RECORDED";
private String notRecordedFeedback = "NOT RECORDED";
private int countDISDAQs = 0;
private int DISDAQtime = 6;
private int soundEnabled = 0;
private int childrensVersion = 0;
private int conditionDifficulty = 5;
    
   public RunSettings(){}

   public RunSettings (boolean rEnabled, boolean cEnabled, boolean eEnabled, String rAcq, String cAcq, String eAcq, int cSpeed, int order, int repetitions, String id, String path, String name, int signal, String key, String left, String right, String confirm, boolean timeout, String nBack, String cFeedback, String iFeedback, String rFeedback, String nFeedback, boolean cD, String dT, boolean sound, boolean child, int condDiff) {
        restEnabled = rEnabled ? 1:0;
        controlEnabled = cEnabled ? 1:0;
        experimentalEnabled = eEnabled ? 1:0;
        restAcquisitions = Integer.parseInt(rAcq);
        controlAcquisitions = Integer.parseInt(cAcq);
        experimentalAcquisitions = Integer.parseInt(eAcq);
        controlConditionSpeed = cSpeed;
        orderOfConditions = order;
        repetitionsWithin = repetitions;
        subjID = id;
        filePath = path;
        fileName = name;
        scannerSignal = signal;
        scannerKey = key;
        leftKey = left;
        rightKey = right;
        confirmKey = confirm;
        controlTimeout = timeout ? 1:0;
        nBackNum = Integer.parseInt(nBack);
        correctFeedback = cFeedback;
        incorrectFeedback = iFeedback;
        recordedFeedback = rFeedback;
        notRecordedFeedback = nFeedback;
        countDISDAQs = cD ? 1:0;
        DISDAQtime = Integer.parseInt(dT);
        soundEnabled = sound ? 1:0;
        childrensVersion = child ? 1:0;
        conditionDifficulty = condDiff;
    }
   
   public void setChildrensVersion (int in){
       childrensVersion = in;       
   }
   public int getChildrensVersion (){
       return childrensVersion;
   }
   public void setConditionDifficulty(int in){
       conditionDifficulty = in;
   }
   public int getConditionDifficulty(){
       return conditionDifficulty;
   }
   
   public void setSoundEnabled(int in){
       soundEnabled = in;
   }
    public void setCountDISDAQs(int in){
        countDISDAQs = in;
    }
    public void setDISDAQtime(int in){
        DISDAQtime = in;
    }
    public void setRestEnabled(int in){
        restEnabled = in;
    }
    public void setControlEnabled(int in){
        controlEnabled=in;
    }
    public void setExperimentalEnabled(int in){
        experimentalEnabled = in;
    }
    public void setRestAcquisitions(int in){
        restAcquisitions = in;
    }
    public void setControlAcquisitions(int in){
        controlAcquisitions = in;
    }
    public void setExperimentalAcquisitions(int in){
        experimentalAcquisitions = in;
    }
    public void setControlConditionSpeed(int in){
        controlConditionSpeed = in;
    }
    public void setOrderOfConditions(int in){
        orderOfConditions = in;
    }
    public void setRepetitionsWithin(int in){
        repetitionsWithin = in;
    }
    public void setSubjID(String in){
        subjID = in;
    }
    public void setFilePath(String in){
        filePath = in;
    }
    public void setFileName(String in){
        fileName = in;
    }
    public void setScannerSignal(int in){
        scannerSignal = in;
    }
    public void setScannerKey(String in){
        scannerKey = in;
    }
    public void setLeftKey(String in){
        leftKey = in;
    }
    public void setRightKey(String in){
        rightKey = in;
    }
    public void setConfirmKey(String in){
        confirmKey = in;
    }
    public void setControlTimeout(int in){
        controlTimeout = in;
    }
    public void setNBackNum(int in){
        nBackNum = in;
    }
    public void setCorrectFeedback(String in){
        correctFeedback = in;
    }
    public void setIncorrectFeedback(String in){
        incorrectFeedback = in;
    }
    public void setRecordedFeedback(String in){
        recordedFeedback = in;
    }
    public void setNotRecordedFeedback(String in){
        notRecordedFeedback = in;
    }
    
    public int getRestEnabled(){
        return restEnabled;
    }
    public int getControlEnabled(){
        return controlEnabled;
    }
    public int getExperimentalEnabled(){
        return experimentalEnabled;
    }
    public int getRestAcquisitions(){
        return restAcquisitions;
    }
    public int getControlAcquisitions(){
        return controlAcquisitions;
    }
    public int getExperimentalAcquisitions(){
        return experimentalAcquisitions;
    }
    public int getControlConditionSpeed(){
        return controlConditionSpeed;
    }
    public int getOrderOfConditions(){
        return orderOfConditions;
    }
    public int getRepetitionsWithin(){
        return repetitionsWithin;
    }
    public String getSubjID(){
        return subjID;
    }
    public String getFilePath(){
        return filePath;
    }
    public String getFileName(){
        return fileName;
    }
    public int getScannerSignal(){
        return scannerSignal;
    }
    public String getScannerKey(){
        return scannerKey;
    }
    public String getLeftKey(){
        return leftKey;
    }
    public String getRightKey(){
        return rightKey;
    }
    public String getConfirmKey(){
        return confirmKey;
    }
    public int getControlTimeout(){
        return controlTimeout;
    }
    public int getNBackNum(){
        return nBackNum;
    }
    public String getCorrectFeedback(){
        return correctFeedback;
    }
    public String getIncorrectFeedback(){
        return incorrectFeedback;
    }
    public String getRecordedFeedback(){
        return recordedFeedback;
    }
    public String getNotRecordedFeedback(){
        return notRecordedFeedback;
    }
     public int getCountDISDAQs(){
        return countDISDAQs;
    }
    public int getDISDAQtime(){
        return DISDAQtime;
    }
    public int getSoundEnabled(){
        return soundEnabled;
    }
  
    public String write(){
        String out = "";
        
        out += 
        "Rest Enabled~" + restEnabled + "\n" +
        "Control Enabled~" + controlEnabled + "\n" +
        "Experimental Enabled~" + experimentalEnabled + "\n" +
        "Rest Acquisitions~" + restAcquisitions + "\n" +
        "Control Acquisitions~" + controlAcquisitions + "\n" +
        "Experimental Acquisitions~" + experimentalAcquisitions + "\n" +
        "Control Speed~" + controlConditionSpeed + "\n" +
        "Order~" + orderOfConditions + "\n" +
        "Repetitions~" + repetitionsWithin + "\n" +
        "Subject ID~" + subjID + "\n" +
        "File Path~" + filePath + "\n" +
        "File Name~" + fileName + "\n" +
        "Scanner Signal~" + scannerSignal + "\n" +
        "Scanner Key~" + scannerKey + "\n" +
        "Left Key~" + leftKey + "\n" +
        "Right Key~" + rightKey + "\n" +
        "Confirm Key~" + confirmKey + "\n" +
        "Control Timeout~" + controlTimeout + "\n" +
        "N-Back Number~" + nBackNum + "\n" +
        "Correct Feedback~" + correctFeedback + "\n" +
        "Incorrect Feedback~" + incorrectFeedback + "\n" +
        "Recorded Feedback~" + recordedFeedback + "\n" +
        "Not Recorded Feedback~" + notRecordedFeedback + "\n" +
        "Count DISDAQs~" + countDISDAQs + "\n" + 
        "DISDAQ Acquisitions~" + DISDAQtime + "\n" + 
        "Sound Enabled~" + soundEnabled + "\n" +
        "Childrens Version~" + childrensVersion + "\n" + 
        "Experimental Condition Difficulty~" + conditionDifficulty;
        
        return out;
    }
    public void read(String in){
        StringTokenizer st = new StringTokenizer(in, "~");
        String field = st.nextToken();
        String value = st.nextToken();
        
        switch (field){
            case "Rest Enabled": restEnabled = Integer.parseInt(value); break;
            case "Control Enabled": controlEnabled = Integer.parseInt(value); break;
            case "Experimental Enabled": experimentalEnabled = Integer.parseInt(value); break;
            case "Rest Acquisitions": restAcquisitions = Integer.parseInt(value); break;
            case "Control Acquisitions": controlAcquisitions = Integer.parseInt(value); break;
            case "Experimental Acquisitions": experimentalAcquisitions = Integer.parseInt(value); break;
            case "Control Speed": controlConditionSpeed = Integer.parseInt(value); break;
            case "Order": orderOfConditions = Integer.parseInt(value); break;
            case "Repetitions": repetitionsWithin = Integer.parseInt(value); break;
            case "Subject ID": subjID = value; break;
            case "File Path": filePath = value; break;
            case "File Name": fileName = value; break;
            case "Scanner Signal": scannerSignal = Integer.parseInt(value); break;
            case "Scanner Key": scannerKey = value; break;
            case "Left Key": leftKey = value; break;
            case "Right Key": rightKey = value; break;
            case "Confirm Key": confirmKey = value; break;
            case "Control Timeout": controlTimeout = Integer.parseInt(value); break;
            case "N-Back Number": nBackNum = Integer.parseInt(value); break;
            case "Correct Feedback": correctFeedback = value; break;
            case "Incorrect Feedback": incorrectFeedback = value; break;
            case "Recorded Feedback": recordedFeedback = value; break;
            case "Not Recorded Feedback": notRecordedFeedback = value; break;
            case "Count DISDAQs": countDISDAQs = Integer.parseInt(value); break;
            case "DISDAQ Acquisitions": DISDAQtime = Integer.parseInt(value); break;
            case "Sound Enabled": soundEnabled = Integer.parseInt(value); break;
            case "Childrens Version": childrensVersion = Integer.parseInt(value);break;
            case "Experimental Condition Difficulty": conditionDifficulty = Integer.parseInt(value); break;
        }
        
        //while(st.hasMoreElements()){
         //   System.out.println(st.nextToken());
       // }
    }
}
