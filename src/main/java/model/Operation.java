package model;

import java.time.LocalDateTime;

public class Operation {
    public enum TypeOperation {
        DEPOT,
        RETRAIT,
        VIREMENT
    }

    private int id;
    private TypeOperation type;
    private double montant;
    private LocalDateTime date;
    private int compteSource;
    private Integer compteDestination; 

    public Operation(int id,
                     TypeOperation type,
                     double montant,
                     LocalDateTime date,
                     int compteSource,
                     Integer compteDestination) {
        this.id = id;
        this.type = type;
        this.montant = montant;
        this.date = date;
        this.compteSource = compteSource;
        this.compteDestination = compteDestination;
    }

    public int getId() { return id; }
    public TypeOperation getType() { return type; }
    public double getMontant() { return montant; }
    public LocalDateTime getDate() { return date; }
    public int getCompteSource() { return compteSource; }
    public Integer getCompteDestination() { return compteDestination; }
}