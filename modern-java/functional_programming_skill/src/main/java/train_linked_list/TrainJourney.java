package train_linked_list;

public class TrainJourney {
    public int price;
    public TrainJourney onward;

    public TrainJourney(int price, TrainJourney onward){
        this.price = price;
        this.onward = onward;
    }
}
