package sources.hex;

import java.util.concurrent.ConcurrentHashMap;

import main.java.hex.IPlateau;

public class Plateau implements IPlateau {
	private final static int TAILLE_MAX = 26, NB_JOUEURS = 2, PREMIERE_COLONNE = 'A', PREMIERE_LIGNE = '1';
	/**le premier joueur relie la premiere et la derniere ligne*/
	/**le second joueur relie la premiere et la derniere colonne*/
	private ConcurrentHashMap<XY,Pion> coord;
	private Pion[][] t; //Tableau representant le Plateau
	private boolean estFinie, j1, j2;  //etat de fin de la partie etat du joueur 1 et 2 true = joueur false = robot
	private Pion [] p; //tableau pour connaître le pion du joueur en cours
	private Integer gagnant; //Valeur qui indique le gagnant de la partie
	private int nbCoups, mode, joueur = 0; //Nombre de coups dans la partie mode de jeu de la partie prochain � jouer
	
	public Plateau(int taille, int mode) {

		assert taille > 0 && taille <= TAILLE_MAX;

		t = new Pion [taille][taille];
		this.gagnant = null;
		this.estFinie = false;
		this.nbCoups = 0;
		this.mode = mode;
		this.coord = new ConcurrentHashMap<XY, Pion>();

		for (int lig = 0; lig < taille(); ++lig)
			for (int col = 0; col < taille(); ++col)
				t[col][lig] = Pion.Vide;
		p = new Pion[2];
		p[0] = Pion.Croix;
		p[1] = Pion.Rond;
		if(mode == 1) { //si mode = 1  alors il y a un joueur et une IA
			this.j1 = true;
			this.j2 = true;
		}
		else if(mode == 2) { //si mode = 2  alors il y a un joueur et une IA
			this.j1 = false;
			this.j2 = true;
		}
		else { //sinon il n'y a que des IA
			this.j1 = false;
			this.j2 = false;
		}
	}
	
	private void suivant() {
		joueur = (joueur +1) % NB_JOUEURS;
	}
	@Override
	public int getMode() {
		return mode;
	}

	/**
	 * Fonction inverserPion() permettant d'inverser les Pions des joueurs entre eux
	 * Selon la règle du jeu de Hex, le 2ème joueur a le droit de pouvoir échanger ses Pions
	 * avec le premier joueur suite au premier coup du tout premier joueur
	 * return : boolean
	 * */
	@Override
	public boolean inverserPion() {
		if(this.getJoueur() == 2 && nbCoups == 1) {
			p[1] = Pion.Croix;
			p[0] = Pion.Rond;
			suivant();
			return true;
		}
		return false;
	}
	@Override
	public int getJoueur() {
		return joueur + 1;
	}

	/**
	 * Fonction jouer(String coord) permettant de faire jouer un joueur avec la coordonée
	 * entrée en paramètre
	 * Tant que la partie n'est pas finie, c'est au joueur suivant.
	 * param[in] : String coord
	 * */

	@Override
	public void jouer(String coord) {
		Pion Pion = p[joueur]; //Nous recuperons le pion du joueurs courant
		int col = getColonne (coord); //nous recuperons les lignes et les collones des coordonnees
		int lig = getLigne(coord);
		t[col][lig] = Pion;
		this.estFinie();//Nous verifions si la partie est finis
		suivant(); //nous passons au joueur suivant
		nbCoups++; //nous indiquons qu'un coup a été joué
	}

	public Pion getPionJ1() {
		return p[0];
	}
	public Pion getPionJ2() {
		return p[1];
	}
	public boolean getJ1() {
		return this.j1;
	}
	public boolean getJ2() {
		return this.j2;
	}
	@Override
	public int getCoups() {
		return nbCoups;
	}

	@Override
	public boolean jCourantEstJoueur() {
		if(joueur == 0)return j1;
		return j2;
	}

	/**
	Fonction jouerrobot() permettant de faire jouer l'IA en choisissant des valeurs aléatoires
	 selon le déroulement de la partie et les possibilités
	*/
	@Override
	public void jouerrobot() {
		Pion Pion = p[joueur];
		int col,lig;
		do {
			col = (int) (0 + (Math.random() * (this.t.length - 0)));
			lig = (int) (0 + (Math.random() * (this.t.length - 0)));
		}while(t[col][lig] != Pion.Vide );
		t[col][lig] = Pion;
		this.estFinie();
		suivant();
		nbCoups++;
	}
	
	@Override
	public boolean FIN() {
		return this.estFinie;
		
	}
	public static int getTaille(String pos) {
		int taille = (int) Math.sqrt(pos.length());
		assert taille * taille == pos.length();
		return taille;
	}

	/**
	 * Fonction estValide(String coord) permettant de savoir si la coordonnée entrée en paramètre
	 * est bien une coordonnée valide (Si le premier caractère est bien un int qui est compris entre 1 et la taille du plateau
	 * Et si le deuxième caractère est bien une lettre.
	 * return : boolean
	 * */
	@Override
	public boolean estValide(String coord) {
		if (coord.length() != 2 && coord.length() != 3)
			return false;
		try {
            Double num = Double.parseDouble(coord.substring(1));
        } catch (NumberFormatException e) {
            return false;
        }
		int col = getColonne (coord);
		int lig = getLigne(coord);
		if (col < 0 || col >= taille())
			return false;
		if (lig < 0 || lig >= taille())
			return false;
		
		return true;
	}

	/**
	Fonction permettant de savoir si la case à la coordonnées entrée est vide.
	in : String coord
	return : boolean
	*/
	@Override
	public boolean estVide(String coord) {
		if(this.getCase(coord) != Pion.Vide)
			return false;
		return true;
	}
	
	public Pion getCase(String coord) {
		assert estValide(coord);
		int col = getColonne (coord);
		int lig = getLigne(coord);
		return t[col][lig];
	}

	private int getColonne(String coord) {
		return coord.toUpperCase().charAt(0) - PREMIERE_COLONNE; // ex 'B' -'A' == 1
	}
	
	private int getLigne(String coord) {
		String s = coord.substring(1);
		int i = Integer.parseInt(s) +'0';
		return  i - PREMIERE_LIGNE; // ex '2' - '1' == 1
	}

	/*
	Fonction retournant le gagnant de la partie
	*/
	public Integer getGagnant() {
		return gagnant;
	}

	public Plateau(int taille, String pos) {
		assert taille > 0 && taille <= TAILLE_MAX;
		t = new Pion [taille][taille];
		this.coord = new ConcurrentHashMap<XY, Pion>();
		String[] lignes = decouper(pos);
		
		for (int lig = 0; lig < taille(); ++lig)
			for (int col = 0; col < taille(); ++col)
				t[col][lig] = Pion.get(lignes[lig].charAt(col));
		
		if (getNb(Pion.Croix) != getNb(Pion.Rond) &&
			getNb(Pion.Croix) != (getNb(Pion.Rond)+1) &&
					getNb(Pion.Croix) != (getNb(Pion.Rond)-1))
			throw new IllegalArgumentException(
					"position non valide");
	}

	public int getNb(Pion Pion) {
		int nb = 0;
		for (Pion [] ligne : t)
			for (Pion p : ligne)
				if (p == Pion)
					++nb;
		return nb;
	}

	public int taille() {
		return t.length;
	}

	private String espaces(int n) {
		String s = "";
		for(int i = 0; i < n; ++i)
			s += " ";
		return s;
	}

	@Override
	public String toString() {
		String s = "";
		for (int i = 0; i < taille(); ++i)
			s += " "+(char)( 'A' + i);
		s += '\n';
		for (int lig = 0; lig < taille(); ++lig) {
			s += lig+1 + espaces (lig);
			for (int col = 0; col < taille(); ++col)
				s += " "+ t[col][lig];
			s +='\n';
		}
		return s;
	}

	public static String[] decouper(String pos) {
		int taille = getTaille(pos);
		String[] lignes = new String[taille];
		for (int i = 0; i <taille; ++i)
			lignes[i] = pos.substring(i*taille,
					(i+1)*taille);
		return lignes;
		
	}

	/**
	 * Fonction estFinie() permettant de savoir si à chaque coup joué par un joueur, la partie est finie ou non
	 * */
	
	public void estFinie() {
		Pion Pion = p[joueur];
		this.coord.clear();
			if(Pion == Pion.Croix) {
					this.TrouverCroix(Pion);
				}
				if(Pion == Pion.Rond) {
					this.TrouverRond(Pion);
				}
		this.chercher();
		if(this.gagner() == true ) {
			this.estFinie = true;
			this.gagnant = this.joueur+1;
			}
	}

	public boolean gagner() { 
		for(int x = 0; x < this.taille(); x++) { //Pour chaque pion nous verifions si les conditions de victoires sont remplis
			XY finCroix = new XY(x,this.taille() - 1);
			if(this.coord.containsKey(finCroix) && this.coord.get(finCroix) == Pion.Croix) { //Pour les croix : s'il y une croix a la dernière ligne dans le chemin trouve
				return true;
			}
			XY finRond = new XY(this.taille() - 1, x);
			if(this.coord.containsKey(finRond) && this.coord.get(finRond) == Pion.Rond) { //Pour les ronds : s'il y un rond a la dernière colonne dans le chemin trouve
				return true;
			}
		}
		return false;
	}
	
	public void TrouverCroix(Pion p){
		for(int j = 0;j < this.taille(); j++) {
			if(this.t[j][0] == p) {
				XY h = new XY(j,0);
				if(!this.coord.containsKey(h)) { //ajoute une position dans le chemin s'il a trouve une croix sur la première ligne
					this.coord.put(h, p);
				}
			}
		}
	}
	
	public void TrouverRond(Pion p){
		for(int j = 0;j < this.taille(); j++) {
			if(this.t[0][j] == p) {
				XY h = new XY(0, j);
				if(!this.coord.containsKey(h)) { //ajoute une position dans le chemin s'il a trouve un rond sur la première colonne
					this.coord.put(h, p);
					}
				}
			}
	}

	/*
	Fonction permettant de chercher le chemin gagnant
	 */

	public void chercher() {
		ConcurrentHashMap<XY,Pion> tmp = new ConcurrentHashMap<XY, Pion>();
		this.coord.forEach( (key, value) ->{ //nous instancions un chemin tmp
			tmp.put(key, value); //nous lui ajoutons la position qui permet de commencer le chemin
		});
			
			while(!tmp.isEmpty()) { //nous lui ajoutons la position qui permet de commencer le chemin
				tmp.forEach( (key, value) ->{ //alors nous cherchons dans chaque case autour de la position courante
				for(int x = key.x - 1; x<=key.x + 1; x++) {
					if(x >= 0 && x <= this.taille() - 1) {
						if(x == key.x-1) {
							for(int y = key.y; y <= key.y + 1; y++) {
								if(y >= 0 && y <= this.taille() - 1) {
									XY h = new XY(x, y);
									if(this.t[x][y] == value && !this.coord.containsKey(h)) { //si nous trouvons une autre case du même pion et qu'll n'est pas le chemin
										tmp.put(h, value); //nous l'ajoutons au chemin tmp
									}
								}
							}
							
						}
						if(x == key.x) {
							for(int y = key.y - 1;y <= key.y + 1; y++) {
								if(y >= 0 && y <= this.taille() - 1 && y != key.y) {
									XY h = new XY(x, y);
									if(this.t[x][y] == value && !this.coord.containsKey(h)) {
										tmp.put(h, value);
									}
								}
							}
							
						}
						if(x == key.x+1) {
							for(int y = key.y - 1;y <= key.y; y++) {
								if(y >= 0 && y <= this.taille() - 1) {
									XY h = new XY(x, y);
									if(this.t[x][y] == value && !this.coord.containsKey(h)) {
										tmp.put(h, value);
									}
								}
							}
							
						}
					}
						
				}
				this.coord.put(key, value); //une fois exploite nous l'ajoutons au chemin principale
				tmp.remove(key); //et nous le retirons de tmp
			});
			}
	}
	/*Classe XY de coordonnees*/
	public static class XY {
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			XY other = (XY) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
		private int x;
		private int y;
		public XY(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}



}
