package org.example.domainmodel;
import java.io.Serializable;

public record Position(int x, int y) implements Serializable {
}