package dto;

import lombok.Data;

@Data
public class IngredientsDto {
    String[] ingredients;

    public IngredientsDto(String[] ingredients) {
        this.ingredients = ingredients;
    }
}
