//Nick Kroeger

import java.util.*;

public class FibonacciHeap
{
    private Node maxNode;
    private int numberOfNodes;

    FibonacciHeap(){ //constructor
        maxNode = null;
        numberOfNodes = 0;
    }

    public void increaseKey(Node n, int k){
        //update frequency associated with a keyword/node
        Node parent = n.parent;
        n.setFrequency(k);
        updateTreeAfterIncreaseKey(n, parent);
    }

    private void updateTreeAfterIncreaseKey(Node n, Node parent){
        //When increasing a key, check if the parent is greater to trigger a cascading cut
        if((parent != null) && (parent.getFrequency() < n.getFrequency())) {
            cut(n, parent);
            cascadingCut(parent);
        }
        if(newNodeIsBiggerThanMaxNode(n))
            maxNode = n;
    }

    private void cut(Node child, Node parent){
        //arbitrary removal of a node given the child and parent
        parent.degree--;
        removeNodeFromLevelList(child);
        changeChildPointerOfParent(child, parent);
        moveChildToRootList(child);
        child.childCut = false;
    }

    private void removeNodeFromLevelList(Node n){
        //helper function used to remove an arbitrary node from the circular list in a tree level
        n.prev.next = n.next;
        n.next.prev = n.prev;
    }

    private void changeChildPointerOfParent(Node child, Node parent){
        //helper function for "cut" function
        if(parent.child == child)
            parent.child = child.next;
        if(parent.degree == 0)
            parent.child = null;
    }

    private void moveChildToRootList(Node child){
        //helper function to cut out a node from arbitrary position in the trees
        // and reinsert into the root list.
        child.prev = maxNode;
        child.next = maxNode.next;
        maxNode.next = child;
        child.next.prev = child;

        child.parent = null;
    }

    private void cascadingCut(Node n){
        //triggered from losing two children
        if(n.parent != null) {
            if(n.childCut == true){
                cut(n, n.parent);
                cascadingCut(n.parent);
            }
            else
                n.childCut = true;
        }
    }

    public void insert(Node node){
        //insert node into heap at top level list.
        if(heapIsEmpty())
            maxNode = node;
        else{ //add node to left/right circular list
            addNewNodeToTopLevelList(node);

            if(newNodeIsBiggerThanMaxNode(node))
                maxNode = node;
        }
        numberOfNodes++;
    }

    private boolean heapIsEmpty(){
        return maxNode == null;
    }

    private void addNewNodeToTopLevelList(Node n){
        //helper function specifically for inserting a node in to the fib heap
        n.prev = maxNode;
        n.next = maxNode.next;
        maxNode.next = n;
        //create the circular level list pointers
        if (n.next != null)
            n.next.prev = n;
        else{//if only 2 elements in the list now, add pointers to end
            n.next = maxNode;
            maxNode.prev = n;
        }
    }

    private boolean newNodeIsBiggerThanMaxNode(Node n){
        //helper function for readability purposes
        return n.getFrequency() > maxNode.getFrequency();
    }

    public Node removeMax(){
        //When removing the max node in a Fib Heap, we need to pairwise combine all the current
        //nodes in the heap to retain Fib Heap properties
        Node tempMaxNode = maxNode;
        if(tempMaxNode == null)
            return null;
        else{
            Node tempNext;
            Node childOfMax = tempMaxNode.child;
            int maxNodesNumChildren = tempMaxNode.degree;

            while (maxNodesNumChildren >= 1) {
                tempNext = childOfMax.next;
                removeNodeFromLevelList(childOfMax);
                moveChildToRootList(childOfMax);
                childOfMax = tempNext;
                maxNodesNumChildren--;
            }
            removeNodeFromLevelList(tempMaxNode);

            if(tempMaxNode != tempMaxNode.next){
                maxNode = tempMaxNode.next;
                pairwiseCombine();
            }
            else
                maxNode = null;
            numberOfNodes--;
            return tempMaxNode;
        }
    }


    public void pairwiseCombine()
    {
        //Triggered from a remove max.
        //pairwise combine min trees whose roots have equal degree using a tree table.
        //tableSize set to be sufficiently large.
        int tableSize = 50;

        ArrayList<Node> treeTable = new ArrayList<Node>(tableSize);
        fillTreeTable(treeTable, tableSize);

        setTableValues(treeTable);

        combineEntriesAndUpdateMax(treeTable);

    }

    private void fillTreeTable(ArrayList<Node> treeTable, int size){
        //helper function for pairwise combine
        for(int i = 0; i < size; i++)
            treeTable.add(null);
    }

    private void setTableValues(ArrayList<Node> treeTable){
        //helper function for pairwise combine to pass through all root nodes and construct treeTable
        int numRoots = getNumRoots();
        Node tempNode = maxNode;
        int tempNodeDegree;

        while(stillMoreRootsToVisit(numRoots)){
            tempNodeDegree = tempNode.degree;

            while(true){
                Node nodeFromTreeTable = treeTable.get(tempNodeDegree);
                if(nodeFromTreeTable == null)
                    break;
                if(nodeFromTreeTable.getFrequency() > tempNode.getFrequency()){
                    Node temp = nodeFromTreeTable;
                    nodeFromTreeTable = tempNode;
                    tempNode = temp;
                }
                makeChild(nodeFromTreeTable, tempNode);
                treeTable.set(tempNodeDegree, null);
                tempNodeDegree++;
            }

            treeTable.set(tempNodeDegree, tempNode);//store pointer in tree table given the degree of the node

            tempNode = tempNode.next;
            numRoots--;
        }
    }

    private int getNumRoots(){
        //helper function for pairwiseCombine
        int roots = 0;
        Node tempNode = maxNode;
        if(maxNode == null)
            return 1;
        else{
            tempNode = tempNode.next;
            roots++;
            while(tempNode != maxNode){
                roots++;
                tempNode = tempNode.next;
            }
        }
        return roots;
    }

    private boolean stillMoreRootsToVisit(int numRoots){
        return numRoots > 0;
    }

    private void combineEntriesAndUpdateMax(ArrayList<Node> treeTable){
        //final phase of pairwise-combine to actually combine heaps and finally update max pointer
        maxNode = null;
        for(int i = 0; i < treeTable.size(); i++){
            Node nodeFromTreeTable = treeTable.get(i);
            if(nodeFromTreeTable == null)
                continue;

            if(maxNode == null)
                maxNode = nodeFromTreeTable;
            else{
                removeNodeFromLevelList(nodeFromTreeTable);

                moveChildToRootList(nodeFromTreeTable);

                if(newNodeIsBiggerThanMaxNode(nodeFromTreeTable))
                    maxNode = nodeFromTreeTable;
            }
        }
    }

    public void makeChild(Node newChild, Node newParent)
    {
        //remove newChild from root list of FibonacciHeap
        removeNodeFromLevelList(newChild);
        newChild.parent = newParent;
        insertIntoLevelList(newChild, newParent);
        //add a child node to parent, increase degree
        newParent.degree++;
        //reset childCut
        newChild.childCut = false;
    }

    private void insertIntoLevelList(Node newChild, Node newParent){
        //helper function for makeChild used to move a node to become the child of another node

        if(newParent.child != null){//if parent has no children
            newChild.prev = newParent.child;
            newChild.next = newParent.child.next;

            newParent.child.next = newChild;
            newChild.next.prev = newChild;
        }else{//if parent has children, append to list, fix pointers
            newParent.child = newChild;
            newChild.next = newChild;
            newChild.prev = newChild;
        }
    }

}
