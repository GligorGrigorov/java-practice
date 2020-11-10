import java.util.Arrays;
public class SandwichExtractor {
    public static String[] extractIngredients(String sandwich){
        String splitter = "bread";
        int first = sandwich.indexOf(splitter);
        int last = sandwich.lastIndexOf(splitter);
        if (first == last){
            return new String[0];
        }
        String[] ingredients = sandwich.subSequence(first+splitter.length(), last).toString().split("-");
        StringBuilder filteredIngredients = new StringBuilder();
        for (int i = 0; i < ingredients.length; i++) {
            if (ingredients[i].compareTo("olives") != 0){
                filteredIngredients.append(ingredients[i]).append(" ");
            }
        }
        ingredients = filteredIngredients.toString().split(" ");
        Arrays.sort(ingredients);
        return ingredients;
    }
    public static void main(String[] args) {
        System.out.println(SocialDistanceMaximizer.maxDistance(new int[]{0,1,0,0,0,0,1,0}));
    }
}
