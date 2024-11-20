package com.ddbpractice.dynamodb;

public class AnimalDTO {
    private String name;
    private String sound;
    private String temperament;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSound() {
        return sound;
    }
    public void setSound(String sound) {
        this.sound = sound;
    }
    public String getTemperament() {
        return temperament;
    }
    public void setTemperament(String temperament) {
        this.temperament = temperament;
    }
    @Override
    public String toString() {
        return "AnimalDTO [name=" + name + ", sound=" + sound + ", temperament=" + temperament + "]";
    }

    
}
