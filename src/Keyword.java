public class Keyword {
    public String word;
    public int rating;

    public Keyword(String word, int rating) {
        this.word = word;
        this.rating = rating;
    }

    public Keyword(String keyword) {
        String[] fields = keyword.split(":");
        this.word = fields[0];
        this.rating = Integer.parseInt(fields[1]);
    }

    @Override
    public String toString() {
        return word + ":" + rating;
    }
}
