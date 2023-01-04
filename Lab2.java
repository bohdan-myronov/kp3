package lab2;

import java.util.ArrayList;
import java.util.Random;

class FunRand {

    /**
     * Generates a random value according to an exponential distribution
     *
     * @param timeMean mean value
     * @return a random value according to an exponential distribution
     */
    public static double Exp(double timeMean) {
        double a = 0;
        while (a == 0) {
            a = Math.random();
        }
        a = -timeMean * Math.log(a);
        return a;
    }

    /**
     * Generates a random value according to a uniform distribution
     *
     * @param timeMin
     * @param timeMax
     * @return a random value according to a uniform distribution
     */
    public static double Unif(double timeMin, double timeMax) {
        double a = 0;
        while (a == 0) {
            a = Math.random();
        }
        a = timeMin + a * (timeMax - timeMin);
        return a;
    }

    /**
     * Generates a random value according to a normal (Gauss) distribution
     *
     * @param timeMean
     * @param timeDeviation
     * @return a random value according to a normal (Gauss) distribution
     */
    public static double Norm(double timeMean, double timeDeviation) {
        double a;
        Random r = new Random();
        a = timeMean + timeDeviation * r.nextGaussian();
        return a;
    }
}

class Element {

    private String name;
    private double tnext;
    private double delayMean, delayDev;
    private String distribution;
    private int quantity;
    private double tcurr;
    private int state;
    private Element nextElement;
    private static int nextId = 0;
    private int id;
    private double averageDeviceLoad;
    public Element() {

        tnext = Double.MAX_VALUE;
        delayMean = 1.0;
        distribution = "exp";
        tcurr = tnext;
        state = 0;
        nextElement = null;
        id = nextId;
        nextId++;
        name = "element" + id;
        averageDeviceLoad = 0;
        
    }

    public Element(double delay) {
        name = "anonymus";
        tnext = 0.0;
        delayMean = delay;
        distribution = "";
        tcurr = tnext;
        state = 0;
        nextElement = null;
        id = nextId;
        nextId++;
        name = "element" + id;
        averageDeviceLoad = 0;
    }

    public Element(String nameOfElement, double delay) {
        name = nameOfElement;
        tnext = 0.0;
        delayMean = delay;
        distribution = "exp";
        tcurr = tnext;
        state = 0;
        nextElement = null;
        id = nextId;
        nextId++;
        name = "element" + id;
        averageDeviceLoad = 0;
    }

    public double getDelay() {
        double delay = getDelayMean();
        if ("exp".equalsIgnoreCase(getDistribution())) {
            delay = FunRand.Exp(getDelayMean());
        } else {
            if ("norm".equalsIgnoreCase(getDistribution())) {
                delay = FunRand.Norm(getDelayMean(),
                        getDelayDev());
            } else {
                if ("unif".equalsIgnoreCase(getDistribution())) {
                    delay = FunRand.Unif(getDelayMean(),
                            getDelayDev());
                } else {
                    if ("".equalsIgnoreCase(getDistribution())) {
                        delay = getDelayMean();
                    }
                }
            }
        }
        return delay;
    }

    public double getDelayDev() {
        return delayDev;
    }

    public void setDelayDev(double delayDev) {
        this.delayDev = delayDev;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTcurr() {
        return tcurr;
    }

    public void setTcurr(double tcurr) {
        this.tcurr = tcurr;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Element getNextElement() {
        return nextElement;
    }

    public void setNextElement(Element nextElement) {
        this.nextElement = nextElement;
    }

    public void inAct() {

    }

    public void outAct() {
        quantity++;
    }

    public double getTnext() {
        return tnext;
    }

    public void setTnext(double tnext) {
        this.tnext = tnext;
    }

    public double getDelayMean() {
        return delayMean;
    }

    public void setDelayMean(double delayMean) {
        this.delayMean = delayMean;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAverageDeviceLoad() {
        return averageDeviceLoad;
    }

    public void setAverageDeviceLoad(double ADL) {
        averageDeviceLoad = ADL;
    }

    public void printResult() {
        System.out.println(getName() + " quantity = " + quantity);
    }

    public void printInfo() {
        System.out.println(getName() + " s-" + state
                + " q-" + quantity
                + " tnext= " + getTnext());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void doStatistics(double delta, double time) {
        double _averageDeviceLoad = getAverageDeviceLoad();
        _averageDeviceLoad += delta / time * getState();
        setAverageDeviceLoad(_averageDeviceLoad);
    }
}

class Create extends Element {

    public Create(double delay) {
        super(delay);
        super.setTnext(0.0); // імітація розпочнеться з події Create
    }

    @Override
    public void outAct() {
        super.outAct();
        super.setTnext(super.getTcurr() + super.getDelay());
        super.getNextElement().inAct();
    }

    @Override
    public void doStatistics(double delta, double time) {
        double _averageDeviceLoad = getAverageDeviceLoad();
        _averageDeviceLoad += delta / time * getState();
        setAverageDeviceLoad(_averageDeviceLoad);
    }
}

class Process extends Element {

    int[] devices; // states
    int[] queues;
    double[] tnexts;
    private int queue, maxqueue, failure;
    private double meanQueue;
    boolean  priorityRoutingEnabled;

    public Process(double delay, int numberOfDevices, boolean priorityRouting) {
        super(delay);
        devices = new int[numberOfDevices];
        queues = new int[numberOfDevices];
        tnexts = new double[numberOfDevices];
        for (int i = 0; i < numberOfDevices;i++){
            tnexts[i] = Double.MAX_VALUE;
        }
        maxqueue = Integer.MAX_VALUE;
        meanQueue = 0.0;
        priorityRoutingEnabled = priorityRouting;
    }

    int getDevicesCount() {
        return devices.length;
    }

    int getDeviceState(int index) {
        return devices[index];
    }

    void setDeviceState(int index, int value) {
        devices[index] = value;
    }

    void setDeviceTnext(int index, double tnext) {
        tnexts[index] = tnext;
    }
    int getQueueOfDevice(int index){
        return queues[index];
    }
    void setQueueOfDevice(int index,int value){
        queues[index] = value;
    }
    @Override
    public double getTnext()
    {
        double leastTnext = 99999999; 
        for (int i = 0; i < getDevicesCount();i++){
            if (tnexts[i] < leastTnext) leastTnext = tnexts[i];
        }
        return leastTnext;
    }
    public int getDeviceIndexByTnextValue(double tnext){
        int id = 0;
        for (int i = 0; i < getDevicesCount();i++){
            if (tnexts[i] == tnext) id = i;
        }
        return id;
    }
    int getLeastBusyQueueDevice(){
        int id = 0;
        int leastBusyQueue = 99999;
        for (int i =0; i< getDevicesCount();i++){
            if (queues[i] < leastBusyQueue) {
                leastBusyQueue = queues[i];
                id = i;
            } 
        }
        return id;
    }

    @Override
    public void inAct() {
        boolean anyFreeDevice = false;
        int freeDevice = 0;
        int leastBusyQueue = 0;
        for (int i = 0; i < getDevicesCount(); i++) {
            if (getDeviceState(i) == 0) {
                setDeviceState(i, 1);
                setDeviceTnext(i, super.getTcurr() + super.getDelay());
                break;
            } else {
                for (int j = 0; j < getDevicesCount(); j++) {
                    if (getDeviceState(j) == 0) {
                        anyFreeDevice = true;
                        freeDevice = j;
                    }
                }
                if (anyFreeDevice){
                    setDeviceState(freeDevice, 1);
                    setDeviceTnext(freeDevice, super.getTcurr() + super.getDelay());
                    break;
                }
                else{
                    freeDevice = getLeastBusyQueueDevice();
                    int queueOfDevice = getQueueOfDevice(freeDevice);
                    if (queueOfDevice< getMaxqueue()){
                        setQueueOfDevice(freeDevice, queueOfDevice+1);
                    }
                    else{
                        failure++;
                        System.out.println("All devices are busy!");

                    }
                    break;
                }
        }
    }
}

@Override
public void outAct() {
        super.outAct();
        double leastTnext = getTnext();
        int deviceId = getDeviceIndexByTnextValue(leastTnext);
        setDeviceTnext(deviceId,Double.MAX_VALUE);
        Element next = super.getNextElement();
        if (next != null) {
            next.inAct();
        }
        setDeviceState(deviceId,0);
        if (getQueueOfDevice(deviceId) > 0) {
            setQueueOfDevice(deviceId, getQueueOfDevice(deviceId) - 1);
            setDeviceState(deviceId,1);
            setDeviceTnext(deviceId, super.getTcurr() + super.getDelay());
        }
    }
    int getOverallQueueLength(){
        int sum = 0;
        for (int i = 0; i < getDevicesCount();i++){
            sum += queues[i];
        }
        return sum;
    }
    public int getFailure() {
        return failure;
    }

    public int getQueue() {
        return queue;
    }

    public void setQueue(int queue) {
        this.queue = queue;
    }

    public int getMaxqueue() {
        return maxqueue;
    }

    public void setMaxqueue(int maxqueue) {
        this.maxqueue = maxqueue;
    }

    @Override
public void printInfo() {
        super.printInfo();
        System.out.println(super.getName() + ": " + getOverallQueueLength() + " left");
        System.out.println("failure = " + this.getFailure());
    }

    @Override
public void doStatistics(double delta, double time) {
        double _averageDeviceLoad = getAverageDeviceLoad();
        _averageDeviceLoad += delta / time * getStatesOfTheDevices();
        setAverageDeviceLoad(_averageDeviceLoad);
        meanQueue = getMeanQueue() + getQueuesLengthOfTheDevices() * delta;
    }
int getStatesOfTheDevices(){
    int sum = 0;
    for (int i = 0; i < getDevicesCount();i++){
        sum += devices[i];
    }
    return sum;
}
int getQueuesLengthOfTheDevices(){
    int sum = 0;
    for (int i = 0; i < getDevicesCount();i++){
        sum += queues[i];
    }
    return sum;
}
    public double getMeanQueue() {
        return meanQueue;

}
}

class Model {

    private ArrayList<Element> list = new ArrayList<>();
    double tnext, tcurr;
    int event;

    public Model(ArrayList<Element> elements) {
        list = elements;
        tnext = 0.0;
        event = 0;
        tcurr = tnext;
    }

    public void simulate(double time) {
        while (tcurr < time) {
            tnext = Double.MAX_VALUE;
            for (Element e : list) {
                if (e.getTnext() < tnext) {
                    tnext = e.getTnext();
                    event = e.getId();
                }
            }
            System.out.println("\nIt's time for event in "
                    + list.get(event).getName()
                      + ", time = " + tnext);
            for (Element e : list) {
                e.doStatistics(tnext - tcurr, time);
            }
            tcurr = tnext;
            for (Element e : list) {
                e.setTcurr(tcurr);
            }
            list.get(event).outAct();
            for (Element e : list) {
                if (e.getTnext() == tcurr) {
                    e.outAct();
                }
            }
            printInfo();
        }
        printResult();
    }

    public void printInfo() {
        for (Element e : list) {
            e.printInfo();
        }
    }

    public void printResult() {
        System.out.println("\n-------------RESULTS-------------");
        int failureCounter = 0;
        int quantity;
        quantity = list.get((0)).getQuantity();
        for (Element e : list) {
            e.printResult();
            if (e instanceof Process) {
                Process p = (Process) e;
                failureCounter += p.getFailure();
                System.out.println("mean length of queue = "
                        + p.getMeanQueue() / tcurr
                        + "\nfailure probability = "
                        + p.getFailure() / (double) p.getQuantity());
                System.out.println("average device load:" + p.getAverageDeviceLoad());
            }
        }
        System.out.println("overall failure probability = " + failureCounter / (double) quantity);
        System.out.println("failure " + failureCounter);
        System.out.println("overall " + quantity);

    }
}

class Lab2 {

    public static void main(String[] args) {
        Create c = new Create(0.1);

        Process p1 = new Process(1,10,false);
        Process p2 = new Process(1,10,false);
        Process p3 = new Process(1,10,false);

        System.out.println("creator = " + c.getId() + " | processor1 = " + p1.getId() + " | processor2 = " + p2.getId() + " | processor3 = " + p3.getId());
        c.setNextElement(p1);
        p1.setNextElement(p2);
        p2.setNextElement(p3);

        p1.setMaxqueue(50);
        p2.setMaxqueue(50);
        p3.setMaxqueue(50);

        c.setName("Creator 0");
        p1.setName("Process 1");
        p2.setName("Process 2");
        p3.setName("Process 3");

        c.setDistribution("exp");
        p1.setDistribution("exp");
        p2.setDistribution("exp");
        p3.setDistribution("exp");

        ArrayList<Element> list = new ArrayList<>();
        list.add(c);
        list.add(p1);
        list.add(p2);
        list.add(p3);

        Model model = new Model(list);
        model.simulate(1000.0);
    }
}
