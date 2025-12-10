package model;

public class Client {
    private int id;
    private String cin;
    private String nom;
    private String prenom;

    public Client(int id, String cin, String nom, String prenom) {
        this.id = id;
        this.cin = cin;
        this.nom = nom;
        this.prenom = prenom;
    }

    public int getId() { return id; }
    public String getCin() { return cin; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }

    public void setCin(String cin) { this.cin = cin; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", cin='" + cin + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                '}';
    }
}