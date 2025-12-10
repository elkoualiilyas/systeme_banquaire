package model;

public abstract class Compte {
    private int numero;
    protected double solde;
    private int clientId; 

    public Compte(int numero, double solde, int clientId) {
        this.numero = numero;
        this.solde = solde;
        this.clientId = clientId;
    }

    public int getNumero() { return numero; }
    public double getSolde() { return solde; }
    public int getClientId() { return clientId; }

    public void deposer(double montant) {
        if (montant <= 0) throw new IllegalArgumentException("Montant invalide");
        solde += montant;
    }

    public void retirer(double montant) {
        if (montant <= 0) throw new IllegalArgumentException("Montant invalide");
        if (montant > solde) throw new IllegalStateException("Solde insuffisant");
        solde -= montant;
    }

    public void transferer(Compte cible, double montant) {
        this.retirer(montant);
        cible.deposer(montant);
    }
}

