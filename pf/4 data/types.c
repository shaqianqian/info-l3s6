/* équivalent du type Ordering' */

enum ordering_e { LT, EQ, GT };

/* équivalent du type TupleEntiers */

struct tupleentiers_paire_s {
    int x;
    int y;
};

struct tupleentiers_triplet_s {
    int x;
    int y;
    int z;
};

struct tupleentiers_s {
    enum tupleentiers_e { PAIRE, TRIPLET } constructeur;
    union tupleentiers_u {
        struct tupleentiers_paire_s   paire;
        struct tupleentiers_triplet_s triplet;
    } contenu;
};

int main() {
    struct tupleentiers_s ex1, ex2;

    /* ex1 = Paire 12 34 */
    ex1.constructeur = PAIRE;
    ex1.contenu.paire.x = 12;
    ex1.contenu.paire.y = 34;

    /* ex2 = Triplet 56 78 90 */
    ex2.constructeur = TRIPLET;
    ex2.contenu.triplet.x = 56;
    ex2.contenu.triplet.y = 78;
    ex2.contenu.triplet.z = 90;

    return 0;
}
