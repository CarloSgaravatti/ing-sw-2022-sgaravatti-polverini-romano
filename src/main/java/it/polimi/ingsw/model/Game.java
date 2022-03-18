package it.polimi.ingsw.model;

import java.util.Scanner;

public class Game {

	private boolean started=false;
	private Player[] players;
	private Round round;
	private Bag bag;
	private Island[] island;
	private int numPlayer;
	private int numRound=0;
	private Cloud[] clouds;

	public Game(){
		numPlayer = 0;
	}

	public void addPlayer(Player p) {
		numPlayer++;
	}

	public void start() {
		if(numPlayer>=2 || numPlayer<=4) {
			started = true;
			doPreoaration();
			newRound();
		}
	}

	public int getNumPlayer(){
		return numPlayer;
	}

	public boolean isStarted() {
		return started;
	}

	public boolean isFinished() {
		return false;
	}

	public Round newRound() {
		Round round = new Round(numRound);// numround lerrore si toglie quando viene creato costruttore round
		numRound++;
		return round;
	}

	public void doPreoaration(){
		int i;

		bag = new Bag();


		//creating list of students and chose the type
		for(i=0;i<Student.numStudents;i++){
			if(i<26){
				bag.insertStudent(new Student(RealmType.YELLOW_GNOMES));
			}
			if(i>=26 && i<52){
				bag.insertStudent(new Student(RealmType.BLUE_UNICORNS));
			}
			if(i>=52 && i<78){
				bag.insertStudent(new Student(RealmType.GREEN_FROGS));

			}
			if(i>=78 && i<104){
				bag.insertStudent(new Student(RealmType.RED_DRAGONS));

			}
			if(i>=104){
				bag.insertStudent(new Student(RealmType.PINK_FAIRES));
			}
		}

		Professor[] professors=new Professor[Professor.numProfessor];


		professors[RealmType.YELLOW_GNOMES.ordinal()] = new Professor(RealmType.YELLOW_GNOMES);

		professors[RealmType.BLUE_UNICORNS.ordinal()] = new Professor(RealmType.BLUE_UNICORNS);

		professors[RealmType.GREEN_FROGS.ordinal()] = new Professor(RealmType.GREEN_FROGS);

		professors[RealmType.RED_DRAGONS.ordinal()] = new Professor(RealmType.RED_DRAGONS);

		professors[RealmType.PINK_FAIRES.ordinal()] = new Professor(RealmType.PINK_FAIRES);



		Player[] players = new Player[numPlayer];
		for(i=0; i<numPlayer; i++){
			// chose nickname and create player
			System.out.println("Chose your nickname: ");
			Scanner s1 = new Scanner(System.in);
			String nick = s1.nextLine();
			players[i]=new Player(nick);
		}

		Island[] islands = new Island[Island.numIslands]; // devo creare isole ma non so come fare perchè forse devo usare Singleisland

		//creation of schools and set for esch students

		School[] schools = new School[numPlayer];
		for(i=0;i<numPlayer;i++){
			if(numPlayer==2 || numPlayer==4) {
				if(i==0) {
					schools[i] = new School(8, TowerType.WHITE);
					players[i].setSchool(schools[i]);
				}
				if(i==1){
					schools[i] = new School(8, TowerType.BLACK);
					players[i].setSchool(schools[i]);
				}
			}
			if(numPlayer==3) {
				if(i==0) {
					schools[i] = new School(6, TowerType.WHITE);
					players[i].setSchool(schools[i]);
				}
				if(i==1){
					schools[i] = new School(6, TowerType.BLACK);
					players[i].setSchool(schools[i]);
				}
				if(i==2){
					schools[i] = new School(6, TowerType.GREY);
					players[i].setSchool(schools[i]);
				}

			}
		}

		int j=1;
		for(i=0;i<numPlayer;i++){
			Assistant[] assistants = new Assistant[Assistant.numAssistants];

			for(i=0;i<Assistant.numAssistants;i++){ //da controllare manca costruttore in assistant e attributo static = 10
				assistants[i] = new Assistant(i+1,j);
				assistants[i+1] = new Assistant(i+2,j);
				i++;
				j++;
			}

			// set assistant deck to all players

			players[i].setAssistants(assistants);
		}

		clouds = new Cloud[numPlayer];

		for(i=0;i<numPlayer;i++){
			clouds[i] = new Cloud(numPlayer);
		}

	}

	public Cloud[] getClouds(){
		return this.clouds;
	}

}
