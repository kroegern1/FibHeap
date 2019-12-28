//Nick Kroeger

public class Node
{
    //node data structure that will be stored inside a fibonacci heap
    private String keyword;
    private int frequency;

    //parent child relationship for each node
    Node parent;
    Node child;

    //prev and next will define the level list they belong to
    Node prev;
    Node next;
    int degree = 0;
    boolean childCut = false;

    Node(String keyword, int frequency){
        this.parent = null;
        this.prev = this;
        this.next = this;

        this.keyword = keyword;
        this.frequency = frequency;
        this.degree = 0;
    }

    //helper functions to improve coding security through encapsulation
    public String getKeyword(){
        return this.keyword;
    }
    public int getFrequency(){
        return this.frequency;
    }
    public void setFrequency(int f){
        this.frequency = f;
    }
}
