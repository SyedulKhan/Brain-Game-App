package org.example.braintraininggame;

//USED IMPORTS

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment {
    //This variable is used to check if there is saved game data
    public static final String PREFS = "CheckSavedGame";

    //These alert dialogues are used to display messages to either confirm their actions
    //or reveal information about the brain game
    private AlertDialog aboutMessage;
    private AlertDialog saveExit;
    private AlertDialog exitWithoutSaving;
    private AlertDialog noSavedData;

    //This key variable is to identify which difficulty the user chooses
    public static int difficulty;

    //These variables are to check if game is saved or user clicks on continue button
    public static boolean gameSaved = false;
    public static boolean ctnBtnClicked = false;

    //These are for saving current activity
    SharedPreferences saveState;
    SharedPreferences.Editor editor;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        final View continueBtn = rootView.findViewById(R.id.continue_btn);
        loadSave();     //this will load saved if there is any when game is reopened
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameSaved == true) {    //this is to check if there is saved game data which then will load the saved game
                    ctnBtnClicked = true;
                    gameSaved = false;
                    startGame(difficulty);
                } else {    //this is to check if there is no saved game data
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("No Saved Game Data is found");
                    builder.setCancelable(false);
                    builder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //nothing
                        }
                    });
                    noSavedData = builder.show();
                }
            }
        });

        View newGameBtn = rootView.findViewById(R.id.newGame_btn);
        newGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //new game is created
                difficultyMenu();
            }
        });

        View aboutBtn = rootView.findViewById(R.id.about_btn);
        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    //opens a dialog box containing rules of the game
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.about_title);
                builder.setMessage(R.string.about_text);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // nothing
                    }
                });
                aboutMessage = builder.show();
            }
        });

        View exitBtn = rootView.findViewById(R.id.exit_btn);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //User can exit the game app
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Save and Exit?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { //user has the option to save game then exit
                        gameSaved = true;
                        save();
                        getActivity().finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {  //user has the option to exit without saving
                        final AlertDialog.Builder builder1 = new AlertDialog.Builder((getActivity()));
                        builder1.setTitle("Are you sure you want to exit without saving?"); //this is confirm if they want to exit without saving
                        builder1.setCancelable(false);
                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                gameSaved = false;
                                delete();
                                getActivity().finish();
                            }
                        });
                        builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //nothing
                            }
                        });
                        exitWithoutSaving = builder1.show();
                    }
                });
                saveExit = builder.show();
            }
        });
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (aboutMessage != null) aboutMessage.dismiss();
    }

    //this opens the difficulty menu which retrieves the value from an array resource
    private void difficultyMenu() {
        new AlertDialog.Builder(getActivity()).setTitle(R.string.new_game_title).setItems(R.array.difficulty,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        startGame(i);
                    }
                }).show();
    }

    //this retrieves the value of the difficulty and starts game according to it
    private void startGame(int i) {
        switch (i) {
            case 0:                                                                // case 0 for novice
                Intent novice = new Intent(getActivity(), GameActivity.class);        //gets novice from game class
                getActivity().startActivity(novice);
                difficulty = 0;
                break;
            case 1:                                                                // case 1 for easy
                Intent easy = new Intent(getActivity(), GameActivity.class);        //gets easy from game class
                getActivity().startActivity(easy);
                difficulty = 1;
                break;
            case 2:                                                                // case 2 for medium
                Intent medium = new Intent(getActivity(), GameActivity.class);        //gets medium from game class
                getActivity().startActivity(medium);
                difficulty = 2;
                break;
            case 3:                                                                // case 3 for novice
                Intent guru = new Intent(getActivity(), GameActivity.class);        //gets guru from game class
                getActivity().startActivity(guru);
                difficulty = 3;
                break;
        }
    }

    //this loads the save data
    //it loads the current difficulty and boolean of saved game
    public void loadSave() {
        SharedPreferences loadSave = getActivity().getSharedPreferences(PREFS, 0);
        Boolean currentSavedData = loadSave.getBoolean("gameSave", false);
        int currentDifficulty = loadSave.getInt("difficulty", 0);
        gameSaved = currentSavedData;
        difficulty = currentDifficulty;
    }

    //saves the current difficulty and boolean
    public void save() {
        Boolean gameSave = gameSaved;
        int diff = difficulty;
        saveState = getActivity().getSharedPreferences(PREFS, MODE_PRIVATE);
        editor = saveState.edit();
        editor.putBoolean("gameSave", gameSave);
        editor.putInt("difficulty", diff);
        editor.apply();
    }

    //deletes all data
    public void delete() {
        SharedPreferences delete = getActivity().getSharedPreferences(PREFS, 0);
        delete.edit().clear().apply();
    }
}
