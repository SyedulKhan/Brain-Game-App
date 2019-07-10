package org.example.braintraininggame;

//IMPORTS

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Random;

public class GameActivity extends Activity {
    //These variables are used for saving the game
    public static final String PREFS = "MySavedGame";
    SharedPreferences saveGame;
    SharedPreferences.Editor editor;

    //These variables are used for selecting randoms numbers
    Random randomNumber = new Random();
    int num1 = randomNumber.nextInt(10);
    int num2 = randomNumber.nextInt(10);
    int num3 = randomNumber.nextInt(10);
    int num4 = randomNumber.nextInt(10);
    int num5 = randomNumber.nextInt(10);
    int num6 = randomNumber.nextInt(10);
    int total;

    //These variables are used for selecting randoms operators
    char[] operators = {'+', '-', '*', '/'};
    char randomOperator = operators[new Random().nextInt(4)];

    //These variables are used for selecting text view components of the game activity
    TextView txtQuestion;
    TextView txtCheckAnswer;
    TextView txtAnswer;
    TextView txtTimer;
    TextView txtCurrentUserScore;
    TextView txtHintsOnOrOff;
    TextView txtScoreMessage;
    TextView txtEndGame;

    //These variables are used for validation and checking the state of the game
    boolean gameFinished = false;
    boolean attemptCorrect = false;
    boolean noAnswer = false;

    //This variable is used for the timer
    CountDownTimer timer;

    //These variables are used for selecting the main buttons in the game
    static Button hash;
    static Button start;

    //These variables are used for checking if the user answer is correct or not
    String userInput;
    String actualAnswer;

    //These variables are used for keeping track of the time
    String timeRemaining;
    int scoreTimeRemaining;

    //These variables are used for keeping track of the score, number of attempts and questions
    public static int currentScore;
    int questionCounter = 0;
    int questionLimit = 10;
    int attempts = 0;
    int attemptsLimit = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        txtEndGame = (TextView)findViewById(R.id.txtf_endGame);
        txtEndGame.setVisibility(View.INVISIBLE);
        txtScoreMessage = (TextView) findViewById(R.id.txtf_scoreText); //This is to hide the score until game ends
        txtScoreMessage.setVisibility(View.INVISIBLE);
        txtCurrentUserScore = (TextView) findViewById(R.id.txtf_currentScore);
        txtCurrentUserScore.setVisibility(View.INVISIBLE);

        if (MainFragment.ctnBtnClicked == true) { //This is to check if user clicks on continue button
            MainFragment.ctnBtnClicked = false;
            loadGame();                           //Then the game loads the saved data
        } else {
            currentScore = 0;
        }

        start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameFinished == false) {    //This button simply starts the game according to the difficulty
                    startGame();                     //that the user chooses
                    questionGenerator();
                } else {
                    //nothing
                }
            }
        });

        hash = (Button) findViewById(R.id.hash);
        hash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtHintsOnOrOff = (TextView) findViewById(R.id.hints_on_or_off);
                String hintsTxt = (String) txtHintsOnOrOff.getText();
                if (hintsTxt.contains("ON")) {              //If user uses hints option then the game will perform different actions
                    scoringCalculationWithHints();
                    if (noAnswer == true) {  //if user doesn't answer in time then new question appears
                        noAnswer = false;
                        txtHintsOnOrOff.setText("OFF");
                        start.performClick();
                    } else if (attempts < attemptsLimit && attemptCorrect == false) {  //if user gets it wrong then they can have another turn
                        attempts++;
                    } else if (attempts == attemptsLimit || attemptCorrect == true) { //if user gets it right then a new question appears
                        attempts = 0;
                        attemptCorrect = false;
                        txtHintsOnOrOff.setText("OFF");
                        timer.cancel();
                        start.performClick();
                    }
                } else if (hintsTxt.contains("OFF")) { //this is when user does not use hints option
                    timer.cancel();                    //game proceeds as normal
                    scoringCalculation();
                    start.performClick();
                }
            }
        });
    }

    //The startGame() method generates new numbers and operators and starts the timer
    public void startGame() {
        num1 = randomNumber.nextInt(10);
        num2 = randomNumber.nextInt(10);
        num3 = randomNumber.nextInt(10);
        num4 = randomNumber.nextInt(10);
        num5 = randomNumber.nextInt(10);
        num6 = randomNumber.nextInt(10);
        randomOperator = operators[new Random().nextInt(4)];
        timer();
    }

    //The questionGenerator() method retrieves the public value of difficulty from the menu to decide
    //which difficulty the user chooses and therefore generates questions according to the difficulty
    public void questionGenerator() {
        txtQuestion = (TextView) findViewById(R.id.question_label);
        //NOVICE
        if (MainFragment.difficulty == 0) {
            switch (randomOperator) {
                case '+':
                    total = num1 + num2;
                    txtQuestion.setText(String.valueOf(" " + num1 + " + " + num2 + " = "));
                    break;
                case '-':
                    total = num1 - num2;
                    txtQuestion.setText(String.valueOf(" " + num1 + " - " + num2 + " = "));
                    break;
                case '*':
                    total = num1 * num2;
                    txtQuestion.setText(String.valueOf(" " + num1 + " * " + num2 + " = "));
                    break;
                case '/':
                    if (num1 == 0 || num2 == 0) {
                        num1++;
                        num2++;
                    }
                    total = num1 / num2;
                    txtQuestion.setText(String.valueOf(" " + num1 + " / " + num2 + " = "));
                    break;
            }
        }
        //EASY
        else if (MainFragment.difficulty == 1) {
            Random randomiser = new Random();
            int integers = randomiser.nextInt(2);
            //2 Integers
            if (integers == 1) {
                switch (randomOperator) {
                    case '+':
                        total = num1 + num2;
                        txtQuestion.setText(String.valueOf(" " + num1 + " + " + num2 + " = "));
                        break;
                    case '-':
                        total = num1 - num2;
                        txtQuestion.setText(String.valueOf(" " + num1 + " - " + num2 + " = "));
                        break;
                    case '*':
                        total = num1 * num2;
                        txtQuestion.setText(String.valueOf(" " + num1 + " * " + num2 + " = "));
                        break;
                    case '/':
                        if (num1 == 0 || num2 == 0) {
                            num1++;
                            num2++;
                        }
                        total = num1 / num2;
                        txtQuestion.setText(String.valueOf(" " + num1 + " / " + num2 + " = "));
                        break;
                }
            }
            //3 Integers
            else {
                switch (randomOperator) {
                    case '+':
                        total = num1 + num2 - num3;
                        txtQuestion.setText(String.valueOf(" " + num1 + "+" + num2 + "-" + num3 + "="));
                    case '-':
                        total = num1 - num2 * num3;
                        txtQuestion.setText(String.valueOf(" " + num1 + "-" + num2 + "*" + num3 + "="));
                    case '*':
                        total = num1 * num2 + num3;
                        txtQuestion.setText(String.valueOf(" " + num1 + "*" + num2 + "+" + num3 + "="));
                    case '/':
                        if (num2 == 0) {
                            num2++;
                        }
                        total = num1 / num2 + num3;
                        txtQuestion.setText(String.valueOf(" " + num1 + "/" + num2 + "+" + num3 + "="));
                }
            }
        }
        //MEDIUM
        else if (MainFragment.difficulty == 2) {
            Random randomiser = new Random();
            int integers = randomiser.nextInt(3);
            //2 Integers
            if (integers == 1) {
                switch (randomOperator) {
                    case '+':
                        total = num1 + num2;
                        txtQuestion.setText(String.valueOf(" " + num1 + " + " + num2 + " = "));
                        break;
                    case '-':
                        total = num1 - num2;
                        txtQuestion.setText(String.valueOf(" " + num1 + " - " + num2 + " = "));
                        break;
                    case '*':
                        total = num1 * num2;
                        txtQuestion.setText(String.valueOf(" " + num1 + " * " + num2 + " = "));
                        break;
                    case '/':
                        if (num1 == 0 || num2 == 0) {
                            num1++;
                            num2++;
                        }
                        total = num1 / num2;
                        txtQuestion.setText(String.valueOf(" " + num1 + " / " + num2 + " = "));
                        break;
                }
            }
            //3 Integers
            else if (integers == 2) {
                switch (randomOperator) {
                    case '+':
                        total = num1 + num2 - num3;
                        txtQuestion.setText(String.valueOf(" " + num1 + "+" + num2 + "-" + num3 + "="));
                    case '-':
                        total = num1 - num2 * num3;
                        txtQuestion.setText(String.valueOf(" " + num1 + "-" + num2 + "*" + num3 + "="));
                    case '*':
                        total = num1 * num2 + num3;
                        txtQuestion.setText(String.valueOf(" " + num1 + "*" + num2 + "+" + num3 + "="));
                    case '/':
                        if (num2 == 0) {
                            num2++;
                        }
                        total = num1 / num2 + num3;
                        txtQuestion.setText(String.valueOf(" " + num1 + "/" + num2 + "+" + num3 + "="));
                }
            }
            //4 Integers
            else {
                switch (randomOperator) {
                    case '+':
                        if (num3 == 0) {
                            num3++;
                        }
                        total = num1 + num2 / num3 - num4;
                        txtQuestion.setText(String.valueOf(" " + num1 + "+" + num2 + "/" + num3 + "-" + num4 + "="));
                    case '-':
                        total = num1 - num2 * num3 + num4;
                        txtQuestion.setText(String.valueOf(" " + num1 + "-" + num2 + "*" + num3 + "+" + num4 + "="));
                    case '*':
                        if (num4 == 0) {
                            num4++;
                        }
                        total = num1 * num2 + num3 / num4;
                        txtQuestion.setText(String.valueOf(" " + num1 + "*" + num2 + "+" + num3 + "/" + num4 + "="));
                    case '/':
                        total = num1 / num2 - num3 + num4;
                        txtQuestion.setText(String.valueOf(" " + num1 + "/" + num2 + "-" + num3 + "+" + num4 + "="));
                }
            }
        }
        //GURU
        else {
            Random randomiser = new Random();
            int integers = randomiser.nextInt(3);
            //4 integers
            if (integers == 1) {
                switch (randomOperator) {
                    case '+':
                        total = num1 + num2 * num3 - num4;
                        txtQuestion.setText(String.valueOf(" " + num1 + "+" + num2 + "*" + num3 + "-" + num4 + "="));
                        break;
                    case '-':
                        if (num4 == 0) {
                            num4++;
                        }
                        total = num1 - num2 + num3 / num4;
                        txtQuestion.setText(String.valueOf(" " + num1 + "-" + num2 + "+" + num3 + "/" + num4 + "="));
                        break;

                    case '*':
                        if (num3 == 0) {
                            num3++;
                        }
                        total = num1 * num2 / num3 - num4;
                        txtQuestion.setText(String.valueOf(" " + num1 + "*" + num2 + "/" + num3 + "-" + num4 + "="));
                        break; //break

                    case '/':
                        if (num2 == 0) {
                            num2++;
                        }
                        total = num1 / num2 + num3 * num4;
                        txtQuestion.setText(String.valueOf(" " + num1 + "/" + num2 + "+" + num3 + "*" + num4 + "="));
                        break;
                }
            }
            //5 integers
            else if (integers == 2){
                switch (randomOperator) {
                    case '+':
                        if (num4 == 0){
                            num4++;
                        }
                        total = num1 + num2 - num3 / num4 * num5;
                        txtQuestion.setText(String.valueOf(" " + num1 + "+" + num2 + "-" + num3 + "/" + num4 + "*" + num5 + "="));
                        break;
                    case '-':
                        if (num5 == 0) {
                            num5++;
                        }
                        total = num1 - num2 + num3 * num4 / num5;
                        txtQuestion.setText(String.valueOf(" " + num1 + "-" + num2 + "+" + num3 + "*" + num4 + "/" + num5 + "="));
                        break;

                    case '*':
                        if (num3 == 0) {
                            num3++;
                        }
                        total = num1 * num2 / num3 - num4 + num5;
                        txtQuestion.setText(String.valueOf(" " + num1 + "*" + num2 + "/" + num3 + "-" + num4 + "+" + num5 + "="));
                        break; //break

                    case '/':
                        if (num2 == 0) {
                            num2++;
                        }
                        total = num1 / num2 + num3 * num4 - num5;
                        txtQuestion.setText(String.valueOf(" " + num1 + "/" + num2 + "+" + num3 + "*" + num4 + "-" + num5 + "="));
                        break;
                }
            }
            //6 integers
            else {
                switch (randomOperator) {
                    case '+':
                        if (num3 == 0){
                            num3++;
                        }
                        total = num1 + num2 / num3 - num4 * num5 + num6;
                        txtQuestion.setText(String.valueOf(" " + num1 + "+" + num2 + "/" + num3 + "-" + num4 + "*" + num5 + "+" + num6 + "="));
                        break;
                    case '-':
                        if (num4 == 0) {
                            num4++;
                        }
                        total = num1 - num2 + num3 / num4 * num5 * num6;
                        txtQuestion.setText(String.valueOf(" " + num1 + "-" + num2 + "+" + num3 + "/" + num4 + "*" + num5 + "*" + num6 + "="));
                        break;

                    case '*':
                        if (num3 == 0) {
                            num3++;
                        }
                        total = num1 * num2 / num3 - num4 + num5 - num6;
                        txtQuestion.setText(String.valueOf(" " + num1 + "*" + num2 + "/" + num3 + "-" + num4 + "+" + num5 + "-" + num6 + "="));
                        break; //break

                    case '/':
                        if (num2 == 0) {
                            num2++;
                        }
                        total = num1 / num2 + num3 * num4 - num5 - num6;
                        txtQuestion.setText(String.valueOf(" " + num1 + "/" + num2 + "+" + num3 + "*" + num4 + "-" + num5 + "-" + num6 + "="));
                        break;
                }
            }
        }

    }

    //The scoringCalculation() method simply does the calculations for each question
    public void scoringCalculation() {
        txtAnswer = (TextView) findViewById(R.id.question_answers);
        txtCheckAnswer = (TextView) findViewById(R.id.incorrect_or_correct);

        userInput = txtAnswer.getText().toString();
        actualAnswer = Integer.toString(total);

        if (userInput.equals("???")) { //This is to check if user does not answer in time
            txtCheckAnswer.setText("TOO SLOW!");
            txtCheckAnswer.setTextColor(Color.RED);
        } else {
            if (userInput.equals(actualAnswer)) { //This is to check if user is correct
                txtCheckAnswer.setText("CORRECT!");
                txtTimer = (TextView) findViewById(R.id.txtf_timer);
                timeRemaining = txtTimer.getText().toString();
                scoreTimeRemaining = Integer.parseInt(timeRemaining);
                if (scoreTimeRemaining == 10) {     //This is to keep track of the score
                    currentScore = currentScore + 100;
                    txtAnswer.setText("???");
                    txtCheckAnswer.setTextColor(Color.GREEN);
                } else {
                    currentScore = currentScore + 100 / (10 - scoreTimeRemaining);
                    txtAnswer.setText("???");
                    txtCheckAnswer.setTextColor(Color.GREEN);
                }
            } else {        //This is to check if user is incorrect
                txtCheckAnswer.setText("WRONG!");
                txtAnswer.setText("???");
                txtCheckAnswer.setTextColor(Color.RED);
            }
        }
        questionCounter++;
        if (questionCounter == questionLimit) { //This is to check if user reaches question limit
            gameFinished = true;
            displayScore(currentScore); //The score will be displayed
            delete();
        }
    }

    //The scoringCalculationWithHints() method also does the calculation but will have different actions when
    //user activates hints option in the game screen
    public void scoringCalculationWithHints() {
        txtAnswer = (TextView) findViewById(R.id.question_answers);
        txtCheckAnswer = (TextView) findViewById(R.id.incorrect_or_correct);

        userInput = txtAnswer.getText().toString();
        actualAnswer = Integer.toString(total);

        if (userInput.equals("???")) {
            txtCheckAnswer.setText("TOO SLOW!");
            txtCheckAnswer.setTextColor(Color.RED);
            noAnswer = true;
            questionCounter++;
        } else {
            if (attempts == attemptsLimit - 1 && Integer.parseInt(userInput) != Integer.parseInt(actualAnswer)) { //user gets it wrong in final attempt
                txtCheckAnswer.setText("WRONG!");
                txtAnswer.setText("???");
                txtCheckAnswer.setTextColor(Color.RED);
                questionCounter++;
            }
            if (Integer.parseInt(userInput) < Integer.parseInt(actualAnswer)) { //user answer is less than actual answer
                txtCheckAnswer.setText("GREATER"); //displays the hint GREATER to notify the user that the answer is higher than their previous answer
                txtAnswer.setText("???");
                txtCheckAnswer.setTextColor(Color.BLUE);
            } else if (Integer.parseInt(userInput) > Integer.parseInt(actualAnswer)) { //user answer is greater than actual answer
                txtCheckAnswer.setText("LESS"); //displays the hint LESS to notify the user that the answer is lees than their previous answer
                txtAnswer.setText("???");
                txtCheckAnswer.setTextColor(Color.BLUE);
            } else if (Integer.parseInt(userInput) == Integer.parseInt(actualAnswer)) {
                txtCheckAnswer.setText("CORRECT!");
                txtAnswer.setText("???");
                txtTimer = (TextView) findViewById(R.id.txtf_timer);
                timeRemaining = txtTimer.getText().toString();
                scoreTimeRemaining = Integer.parseInt(timeRemaining);
                if (scoreTimeRemaining == 10) {
                    currentScore = currentScore + 100;
                    txtAnswer.setText("???");
                    txtCheckAnswer.setTextColor(Color.GREEN);
                } else {
                    currentScore = currentScore + 100 / (10 - scoreTimeRemaining);
                    txtAnswer.setText("???");
                    txtCheckAnswer.setTextColor(Color.GREEN);
                }
                questionCounter++;
                attemptCorrect = true;
            }
        }
        if (questionCounter == questionLimit) {
            gameFinished = true;
            displayScore(currentScore);
            delete();
        }
    }

    //The following methods are for the key pads for which the user enters or deletes numbers
    public void keyPad0(View v) {

        txtAnswer = (TextView) findViewById(R.id.question_answers);
        String txt = (String) txtAnswer.getText();
        if (txt.contains("???")) {
            txt = txt.replace("???", "");
        }
        txtAnswer.setText(txt + "0");
    }

    public void keyPad1(View v) {

        txtAnswer = (TextView) findViewById(R.id.question_answers);
        String txt = (String) txtAnswer.getText();
        if (txt.contains("???")) {
            txt = txt.replace("???", "");
        }
        txtAnswer.setText(txt + "1");
    }

    public void keyPad2(View v) {

        txtAnswer = (TextView) findViewById(R.id.question_answers);
        String txt = (String) txtAnswer.getText();
        if (txt.contains("???")) {
            txt = txt.replace("???", "");
        }
        txtAnswer.setText(txt + "2");
    }

    public void keyPad3(View v) {

        txtAnswer = (TextView) findViewById(R.id.question_answers);
        String txt = (String) txtAnswer.getText();
        if (txt.contains("???")) {
            txt = txt.replace("???", "");
        }
        txtAnswer.setText(txt + "3");
    }

    public void keyPad4(View v) {

        txtAnswer = (TextView) findViewById(R.id.question_answers);
        String txt = (String) txtAnswer.getText();
        if (txt.contains("???")) {
            txt = txt.replace("???", "");
        }
        txtAnswer.setText(txt + "4");
    }

    public void keyPad5(View v) {

        txtAnswer = (TextView) findViewById(R.id.question_answers);
        String txt = (String) txtAnswer.getText();
        if (txt.contains("???")) {
            txt = txt.replace("???", "");
        }
        txtAnswer.setText(txt + "5");
    }

    public void keyPad6(View v) {

        txtAnswer = (TextView) findViewById(R.id.question_answers);
        String txt = (String) txtAnswer.getText();
        if (txt.contains("???")) {
            txt = txt.replace("???", "");
        }
        txtAnswer.setText(txt + "6");
    }

    public void keyPad7(View v) {

        txtAnswer = (TextView) findViewById(R.id.question_answers);
        String txt = (String) txtAnswer.getText();
        if (txt.contains("???")) {
            txt = txt.replace("???", "");
        }
        txtAnswer.setText(txt + "7");
    }

    public void keyPad8(View v) {

        txtAnswer = (TextView) findViewById(R.id.question_answers);
        String txt = (String) txtAnswer.getText();
        if (txt.contains("???")) {
            txt = txt.replace("???", "");
        }
        txtAnswer.setText(txt + "8");
    }

    public void keyPad9(View v) {

        txtAnswer = (TextView) findViewById(R.id.question_answers);
        String txt = (String) txtAnswer.getText();
        if (txt.contains("???")) {
            txt = txt.replace("???", "");
        }
        txtAnswer.setText(txt + "9");
    }

    public void keyPadMinus(View v) {

        txtAnswer = (TextView) findViewById(R.id.question_answers);
        String txt = (String) txtAnswer.getText();
        if (txt.contains("???")) {
            txt = txt.replace("???", "");
        }
        txtAnswer.setText(txt + "-");
    }

    public void keyPadDelete(View v) {
        txtAnswer = (TextView) findViewById(R.id.question_answers);
        String deleteTxt = (String) txtAnswer.getText();
        if (deleteTxt.length() > 0 || (!deleteTxt.equals("???"))) {
            deleteTxt = deleteTxt.substring(0, deleteTxt.length() - 1);
            txtAnswer.setText(deleteTxt);
        }

    }

    //The hints() method is to display the hints text in the game screen
    public void hints(View v) {
        txtHintsOnOrOff = (TextView) findViewById(R.id.hints_on_or_off);
        String hintsTxt = (String) txtHintsOnOrOff.getText();
        if (hintsTxt.contains("OFF")) {
            txtHintsOnOrOff.setText("ON");
        } else if (hintsTxt.contains("ON")) {
            txtHintsOnOrOff.setText("OFF");
        }
    }

    //The displayScore() method displays the user score when game finishes
    public void displayScore(int score) {
        txtCurrentUserScore = findViewById(R.id.txtf_currentScore);
        txtCurrentUserScore.setText(String.valueOf(score));
        txtEndGame = findViewById(R.id.txtf_endGame);
        txtScoreMessage = (TextView) findViewById(R.id.txtf_scoreText);
        txtScoreMessage.setVisibility(View.VISIBLE);  //These reveal the score when game ends
        txtCurrentUserScore.setVisibility(View.VISIBLE);
        txtEndGame.setVisibility(View.VISIBLE);
        txtQuestion.setText("");
        txtAnswer.setText("");
    }

    //The timer() method is for the timer in the game
    public void timer() {
        timer = new CountDownTimer(10000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) { //the timer will tick and will display the seconds
                txtTimer = (TextView) findViewById(R.id.txtf_timer);
                txtTimer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() { //timer resets
                txtTimer = (TextView) findViewById(R.id.txtf_timer);
                txtTimer.setText("10");
                hash.performClick();
            }
        }.start();
    }

    //The saveGame() method saves the game
    //It saves the current score, question, time and counters
    public void saveGame() {
        saveGame = getSharedPreferences(PREFS, MODE_PRIVATE);
        editor = saveGame.edit();
        editor.putInt("savedUserScore", currentScore);
        editor.putInt("secondsLeft", scoreTimeRemaining);
        editor.putInt("questionCounter", questionCounter);
        editor.putInt("total", total);
        editor.putString("question", txtQuestion.getText().toString());
        editor.putString("answer", txtAnswer.getText().toString());
        editor.putString("checkAnswer", txtCheckAnswer.getText().toString());
        editor.apply();
    }

    //The loadGame() method loads the game
    //It loads the score, time, counters and question
    public void loadGame() {
        SharedPreferences loadGame = getSharedPreferences(PREFS, 0);
        int savedScore = loadGame.getInt("savedUserScore", 0);
        String question = loadGame.getString("question", "");
        String answer = loadGame.getString("answer", "");
        String checkAns = loadGame.getString("checkAnswer", "");
        int secondsLeft = loadGame.getInt("secondsLeft", 0);
        int qstnCount = loadGame.getInt("questionCounter", 0);
        int actualAnswer = loadGame.getInt("total", 0);
        txtQuestion = findViewById(R.id.question_label);
        txtQuestion.setText(question);
        txtAnswer = findViewById(R.id.question_answers);
        txtAnswer.setText(answer);
        txtCheckAnswer = findViewById(R.id.incorrect_or_correct);
        txtCheckAnswer.setText(checkAns);
        if (checkAns.contains("CORRECT!")) txtCheckAnswer.setTextColor(Color.GREEN);
        else txtCheckAnswer.setTextColor(Color.RED);
        txtTimer = findViewById(R.id.txtf_timer);
        txtTimer.setText(String.valueOf(secondsLeft));
        total = actualAnswer;
        questionCounter = qstnCount;
        currentScore = savedScore;
        timer();
    }

    //The delete() deletes every data in the game
    public void delete() {
        SharedPreferences delete = getSharedPreferences(PREFS, 0);
        SharedPreferences del = getSharedPreferences(MainFragment.PREFS, 0);
        delete.edit().clear().apply();
        del.edit().clear().apply();
    }

    @Override
    public void onBackPressed() {
        if (timer != null) timer.cancel();
        if (gameFinished == false) {
            final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle("Exit without saving?"); //this is confirm if they want to exit without saving
            builder1.setCancelable(false);
            builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    delete();
                    MainFragment.gameSaved = false;
                    GameActivity.this.finish();
                }
            });
            builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainFragment.gameSaved = true;
                    saveGame();
                    GameActivity.this.finish();
                }
            });
            AlertDialog exitWithoutSaving = builder1.show();
        } else {
            MainFragment.gameSaved = false;
            GameActivity.this.finish();
        }

    }
}
