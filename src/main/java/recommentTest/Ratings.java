package recommentTest;

/**
 * 影评
 */
public class Ratings {
    private Integer userId;
    private Integer movieId;
    private Integer rating;

    public Ratings(Integer userId,Integer movieId,Integer rating){
        this.movieId = movieId;
        this.userId = userId;
        this.rating = rating;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
