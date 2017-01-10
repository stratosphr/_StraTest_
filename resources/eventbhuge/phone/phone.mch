MACHINE
    Phone


SETS Entree={1, 2, 3, 4, 5, 6, 7} ;
     Sortie={8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19} ;
     State={20, 21, 22, 23, 24, 25} ;
     EtCombine={26, 27}

VARIABLES
    Combine,
    NbEssai,
    Etat

INVARIANT
    Combine : EtCombine & NbEssai : 0..2 &
	  Etat : State &
	  ((Etat=24) <=> (Combine=26)) &
	  (Etat=24 => NbEssai=0)

INITIALISATION Combine := 26 || NbEssai := 0 ||
	Etat := 24



OPERATIONS
  r <-- Appel_Qui_Donc =
    PRE Combine=26
    THEN Combine := 27 || r := 8 || Etat := 20
    END ;

  r <-- Raccrocher =
    PRE ((Combine=27) or (Etat=25))
    THEN Etat := 24 || r := 19 ||
	 Combine := 26 || NbEssai := 0
    END ;

  r <-- Delai_Ecoule =
    SELECT Combine=27 & Etat/=25 THEN
      IF Etat=22 THEN Etat := 25 || r := 10
      ELSE IF NbEssai=2 THEN r := 10 || Etat := 25
	   ELSE NbEssai := NbEssai+1 ||
		IF Etat=20 THEN r := 8
		ELSE IF Etat=21 THEN r := 9
		     ELSE r := 16 END
		END
	   END
      END
    END ;

  r <-- Entrer(e) =
    PRE 
	Combine=27 & Etat/=25 & Etat/=24 & e:Entree
    THEN
        NbEssai := 0 ||
        SELECT Etat=20 THEN
          IF e=7 THEN Etat := 21 || r := 9
          ELSE r := 18
          END
        WHEN Etat=21 THEN
          IF e=4 THEN Etat := 22 || r := 17
          ELSE IF e=2 THEN Etat := 23 || r := 13
               ELSE IF e=1 THEN r := 11
                ELSE IF e=3 THEN r := 12
                 ELSE r := 18
                 END
                END
               END
          END
        WHEN Etat=22 THEN
          IF e=7 THEN Etat := 21 || r := 9
          ELSE r := 18 END
        WHEN Etat=23 THEN
          IF e=7 THEN Etat := 21 || r := 9
          ELSE IF e=5 THEN r := 15
               ELSE IF e=6 THEN r := 14
                ELSE r := 18
                END
               END
          END
        END
    END
END
