package train_linked_list;

public class Main {
    /** start 부터 시작해서 마지막에 end 를 추가 */
    static TrainJourney link(TrainJourney start, TrainJourney end){
        if(start == null)
            return end;
        TrainJourney current = start;
        while(current.onward != null){
            current = current.onward;
        }
        current.onward = end;

        return start;
    }

    /** start, end 를 매번 객체를 생성하면서 연결 */
    static TrainJourney append(TrainJourney start, TrainJourney end){
        if (start == null){
            return end;
        }else{
            return new TrainJourney(start.price, append(start.onward, end));
        }
    }

    static void linkTest(){
        // X -> Y
        TrainJourney firstTrainJourney = new TrainJourney(10, new TrainJourney(20, null));

        // Y -> Z
        TrainJourney secondTrainJourney = new TrainJourney(20, new TrainJourney(30, null));

        TrainJourney result = link(firstTrainJourney, secondTrainJourney);

        printTrainJourney(result);
        printTrainJourney(firstTrainJourney);
    }

    static void appendTest(){
        // X -> Y
        TrainJourney firstTrainJourney = new TrainJourney(10, new TrainJourney(20, null));

        // Y -> Z
        TrainJourney secondTrainJourney = new TrainJourney(20, new TrainJourney(30, null));

        TrainJourney result = append(firstTrainJourney, secondTrainJourney);

        printTrainJourney(result);
        printTrainJourney(firstTrainJourney);
    }

    public static void main(String[] args){
//        linkTest();
        appendTest();
    }

    static void printTrainJourney(TrainJourney start){
        TrainJourney copyStart = start;
        while(copyStart != null){
            System.out.print(copyStart.price + " -> ");
            copyStart = copyStart.onward;
        }
        System.out.println("\n");
    }
}
