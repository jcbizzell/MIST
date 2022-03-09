/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package my.MIST;
import java.util.Random;
/**
 *
 * @author Joseph Shaffer
 * @version March 20, 2014
 */
public class TaskGen {
    private int childVer = 0;
    private int conditionDiff = 0;
    private int correctRes;
    private int condition;
    private int difficulty;
    private int expertFlag = 0;
    private int hardFlag = 0;
    private String task;
    private Random rnd = new Random(System.currentTimeMillis());
    
    public TaskGen(int version, int condDiff){
        childVer = version;
        conditionDiff = condDiff;
    }
    
    public String createTask(){
        String problem = "";
       
            int type = rnd.nextInt(6);
        
            
            //Children's version
            if (childVer ==1){
                switch (type){
                case 1: problem = genCat1();
                        break;
                    case 2: problem = genCat1();
                        break;
                    case 3: problem = genCat1();
                        break;
                    case 4: problem = genCat6();
                        break;
                    case 5: problem = genCat6();
                        break;
                    default: problem = genCat6();
                        break;
                } 
            }
            //Adult Version
            else {
            //easy
            if (conditionDiff ==0){
                switch(type){
                    case 1: problem = genCat6();
                        break;
                    case 2: problem = genCat6();
                        break;
                    case 3: problem = genCat6();
                        break;
                    case 4: problem = genCat6();
                        break;
                    case 5: problem = genCat6();
                        break;
                    default: problem = genCat6();
                        break;
                } 
            }
            else if (conditionDiff ==1){
                switch(type){
                    case 1: problem = genCat7();
                        break;
                    case 2: problem = genCat7();
                        break;
                    case 3: problem = genCat7();
                        break;
                    case 4: problem = genCat6();
                        break;
                    case 5: problem = genCat6();
                        break;
                    default: problem = genCat6();
                        break;
                } 
            }
            else if (conditionDiff == 2){
                switch(type){
                    case 1: problem = genCat7();
                        break;
                    case 2: problem = genCat7();
                        break;
                    case 3: problem = genCat3();
                        break;
                    case 4: problem = genCat2();
                        break;
                    case 5: problem = genCat2();
                        break;
                    default: problem = genCat6();
                        break;
                } 
            }
            else if (conditionDiff == 3){
                switch(type){
                    case 1: problem = genCat7();
                        break;
                    case 2: problem = genCat5();
                        break;
                    case 3: problem = genCat3();
                        break;
                    case 4: problem = genCat4();
                        break;
                    case 5: problem = genCat2();
                        break;
                    default: problem = genCat6();
                        break;
                } 
            }
            else if (conditionDiff == 4){
                switch(type){
                    case 1: problem = genCat4();
                        break;
                    case 2: problem = genCat3();
                        break;
                    case 3: problem = genCat3();
                        break;
                    case 4: problem = genCat2();
                        break;
                    case 5: problem = genCat2();
                        break;
                    default: problem = genCat5();
                        break;
                } 
            }
            else {
                if (expertFlag ==0){
                    switch(type){
                    case 1: problem = genCat7();
                        break;
                    case 2: problem = genCat5();
                        break;
                    case 3: problem = genCat3();
                        break;
                    case 4: problem = genCat4();
                        break;
                    case 5: problem = genCat2();
                        break;
                    default: problem = genCat6();
                        break;
                    } 
                }
                else {
                    switch(type){
                        case 1: problem = genCat5();
                            break;
                        case 2: problem = genCat4();
                            break;
                        case 3: problem = genCat5();
                            break;
                        case 4: problem = genCat4();
                            break;
                        case 5: problem = genCat5();
                            break;
                        default: problem = genCat4();
                            break;
                    } 
                }
            }
        }
        task = problem;
        return problem;
    }
 //gives math problem of the form a + b = d, a-b=d, a+b-c=d, or a-b-c=d
    private String genCat1(){
        String problem;
        
        int a = rnd.nextInt(74) + 1;
        int b = rnd.nextInt(75) - 33;
        int d = rnd.nextInt(9);
        int c = a + b - d;
        
        if (c >0){
            if (b > 0) {
                problem = a + " + " + b + " - " + c + " = ?";
            }
            else if (b < 0){
                problem = a + " - " + Math.abs(b) + " - " + c + " = ?";
            }
            else {
                problem = a + " - " + c + " = ?";
            }
        }
        else if (c < 0){
            if (b > 0) {
                problem = a + " + " + b + " + " + Math.abs(c) + " = ?";
            }
            else if (b < 0){
                problem = a + " - " + Math.abs(b) + " + " + Math.abs(c) + " = ?";
            }
            else {
                problem = a + " + " + Math.abs(c) + " = ?";
            }
        }
        else {
            if (b >= 0) {
                problem = a + " + " + b + " = ?";
            }
            else {
                problem = a + " - " + Math.abs(b) + " = ?";
            }
            
        }
        difficulty = 0;
        correctRes = d;
        condition = 0;  //Not used in children's version
        return problem;
    }
    // Gives math problem in the form a x b + c = d, a x b - c= d, or a x b = c
    private String genCat2(){
        String problem;
        int a = rnd.nextInt(11) + 1;
        int b = rnd.nextInt(11) + 1;
        int d = rnd.nextInt(9);
        int c = d - (a * b);
        
        if (c > 0){
            problem = a + " x " + b + " + " + c + " = ?";
        }
        else if (c < 0){
            problem = a + " x " + b + " - " + Math.abs(c) + " = ?";
        }
        else {
           problem = a + " x " + b + " = ?"; 
        }
        difficulty = 0;
        correctRes = d;
        condition = 3;
        return problem;
    }
    
    // Gives math problem in the form of a/b + c - d = e, a/b - d = e, a/b - c -d = e
    private String genCat3(){
        String problem;
        int a = rnd.nextInt(95) + 2;
        int b = rnd.nextInt(11) + 2;
        int d = rnd.nextInt(9)+2;
        int e = rnd.nextInt(9);
        
        double temp = (double)a/(double)b;
        
        while(!(temp == Math.floor(temp) && !Double.isInfinite(temp))){
            a = rnd.nextInt(95) + 2;
            b = rnd.nextInt(11) + 2;
            temp = (double)a/(double)b;
        }
        
        int c = (e+d)-(int)temp;
        
        if (c > 0){
            problem = a + "/" + b + " + " + c + " - " + d + " = ?";
        }
        else if (c < 0){
            problem = a + "/" + b + " - " + Math.abs(c) + " - " + d + " = ?";
        }
        else {
            problem = a + "/" + b + " - " + d + " = ?";
        }
        difficulty = 0;
        correctRes = e;
        condition = 4;
        return problem;
    }
    private String genCat4(){
        String problem;
        int e;
        
        
        if(hardFlag ==1){
            difficulty = 2;
            int n1 = rnd.nextInt(4)+1;
            int d1 = rnd.nextInt(4)+1;
            int n2 = rnd.nextInt(4)+1;
            int d2 = rnd.nextInt(4)+1;
            int n3 = n1 * n2;
            int d3 = d1 * d2;
            double temp = ((double)n1/(double)d1) * ((double)n2/(double)d2) * ((double)d3/(double)n3);
            e = rnd.nextInt(9);
            
            while(!(temp == Math.floor(temp) && !Double.isInfinite(temp))){
                n1 = rnd.nextInt(4)+1;
                d1 = rnd.nextInt(4)+1;
                n2 = rnd.nextInt(4)+1;
                d2 = rnd.nextInt(4)+1;
                n3 = n1 * n2;
                d3 = d1 * d2;
                temp = ((double)n1/(double)d1) * ((double)n2/(double)d2) * ((double)d3/(double)n3);
            }  
            int r = rnd.nextInt(3)+1;
            d3 = d3*r;
            temp = temp *r;
            
            if(temp >= e){
                int d = (int)temp - e;
                
                if(temp ==e){
                    problem = n1 + "/" + d1 + " x " + n2 + "/" + d2 + " x " + d3 + "/" + n3 + " =? ";
                }
                else{
                   problem = n1 + "/" + d1 + " x " + n2 + "/" + d2 + " x " + d3 + "/" + n3 + " - " + d + " = ?"; 
                }
            }
            else {
                int d = e - (int)temp;
                problem = n1 + "/" + d1 + " x " + n2 + "/" + d2 + " x " + d3 + "/" + n3 + " + " + d + " = ?"; 
            }
            
        }
       
        else{
            difficulty = 1;
            int n1 = rnd.nextInt(4)+1;
            int d1 = rnd.nextInt(4)+1;
            int r = rnd.nextInt(4)+1;
            int n2 = d1*r;
            int d2 = n1;
            e = rnd.nextInt(9);
            int a = e-r;
            
            if (a>0){
                problem = n1 + "/" + d1 + " x " + n2 + "/" + d2 + " + " + a + " = ?"; 
            }
            else {
                problem = n1 + "/" + d1 + " x " + n2 + "/" + d2 + " - " + Math.abs(a) + " = ?";
            }
        }
        
        
        
        correctRes = e;
        condition = 6;
        return problem;
    }
    // creates equations of the form a x b/c = d
    private String genCat5(){
        String problem;
        int a = rnd.nextInt(11) + 2;
        int b = rnd.nextInt(11) + 2;
        int d = rnd.nextInt(9);
        
        double temp = (double)(a * b)/(double)d;
        
        while(!(temp == Math.floor(temp) && !Double.isInfinite(temp))|| temp==0){
            a = rnd.nextInt(11) + 2;
            b = rnd.nextInt(11) + 2;
            d = rnd.nextInt(9);
            temp = (double)(a * b)/(double)d;
        }
       int c = (int)temp;
        problem = a + " x " + b + "/" + c + " = ?"; 
        difficulty = 0;
        correctRes = d;
        condition = 5;
        return problem;
    }
    
    //creates equations of the form a+b=c or a-b = c
    private String genCat6(){
        String problem;
        int a = rnd.nextInt(19) + 1;
        int c = rnd.nextInt(9);
                
       while (a == c){
        a = rnd.nextInt(19) + 1;
        c = rnd.nextInt(9);
       } 
          
       int b = a - c;
       
       if (b > 0){
           problem = a + " - " + b + " = ?";          
        }
       else {
           problem = a + " + " + Math.abs(b) + " = ?";  
       }
        difficulty = 0;
        correctRes = c;
        condition = 1;
        return problem;
    }
    
    // creates function of the form "a+b-c=d" or "a-b-c=d" or "axb=d"
        private String genCat7(){
        String problem;
        int d;
        
        int chance = rnd.nextInt(3);
        if (chance == 2) {
            int a = rnd.nextInt(8)+1;
            d = rnd.nextInt(9);
            while (d % a != 0) {
                // Add one so a can never = 0
                a = rnd.nextInt(8)+1;
                d = rnd.nextInt(9);
            }
            int b = d/a;
            problem = a + " x " + b + " = ?";
        }
        else {
            int a = rnd.nextInt(74) + 1;
            int b = rnd.nextInt(75) - 33;
            d = rnd.nextInt(9);
            int c = a + b - d;
            while (c == 0) {
                a = rnd.nextInt(74) + 1;
                b = rnd.nextInt(75) - 33;
                d = rnd.nextInt(9);
                c = a + b - d;
            }

            if (c >0){
                if (b > 0) {
                    problem = a + " + " + b + " - " + c + " = ?";
                }
                else if (b < 0){
                    problem = a + " - " + Math.abs(b) + " - " + c + " = ?";
                }
                else {
                    problem = a + " - " + c + " = ?";
                }
            }
            else if (c < 0){
                if (b > 0) {
                    problem = a + " + " + b + " + " + Math.abs(c) + " = ?";
                }
                else if (b < 0){
                    problem = a + " - " + Math.abs(b) + " + " + Math.abs(c) + " = ?";
                }
                else {
                    problem = a + " + " + Math.abs(c) + " = ?";
                }
            }
            // This should never happen because c should never equal 0
            else {
                if (b >= 0) {
                    problem = a + " + " + b + " = ?";
                }
                else {
                    problem = a + " - " + Math.abs(b) + " = ?";
                }

            }
        }
        difficulty = 0;
        correctRes = d;
        condition = 2;
        return problem;
    }
    
    
    public String getTask(){
        return task;
    }
    public int getResult(){
        return correctRes;
    }
    public int getDifficulty(){
        return difficulty;
    }
    public int getExpert(){
        return expertFlag;
    }
    public int getCondition() {
        return condition;
    }
    public void setExpert(int i){
        expertFlag = i;
    }
    public void setHard (int i){
        hardFlag = i;
    }
    public int getHard(){
        return hardFlag;
    }
    public void setCondition(int i){
        condition = i;
    }  
}

