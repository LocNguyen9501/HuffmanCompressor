import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.lang.StringBuilder;
import java.util.HashMap;
import java.util.Map;
import java.lang.StringBuilder;
import java.io.*;
public class HuffmanCompressor{
  private HuffmanNode root ;
  private ArrayList<HuffmanNode> charList;
  private HashMap<Character,Integer> freqTable = new HashMap<>();
  private HashMap<Character,String> encodedTable = new HashMap<>();
  private int savings;
  
  /* This is the constructor of the HuffmanCompressor
   */
  public HuffmanCompressor(){
    charList = new ArrayList<HuffmanNode>();
  }
  
  /* This is the class to construct HuffmanNode
   */
  private class HuffmanNode{
    private Character charAt;
    private int frequency;
    private HuffmanNode left;
    private HuffmanNode right;
    
    public HuffmanNode(Character charAt, int frequency){
      this.charAt = charAt;
      this.frequency = frequency;
      this.left = null;
      this.right = null;
    }
    
    public Character getCharAt(){
      return this.charAt;
    }
    
    public int getFrequency(){
      return this.frequency;
    }
    
    public void setFrequency(int n){
      this.frequency = n;
    }
    
    public void setLeft(HuffmanNode left){
      this.left = left;
    }
    
    public HuffmanNode getLeft(){
      return this.left;
    }
    
    public void setRight(HuffmanNode right){
      this.right = right;
    }
    
    public HuffmanNode getRight(){
      return this.right;
    }
  }
  
  /* This is the class that construct heap to sort the list of HuffmanNode
   */
  private class Sort{
     private ArrayList<HuffmanNode> list;
     
     public Sort(ArrayList<HuffmanNode> list){
       this.list = list;
     }
     
     public ArrayList<HuffmanNode> getList(){
       return this.list;
     }
     
     public int size(){
       return list.size();
     }
     
     public HuffmanNode remove(int index){
       if(index>size())
         return null;
       else{
       HuffmanNode toReturn = list.get(index);
       list.remove(index);
       return toReturn;
       }
     }
     
     public void insert(HuffmanNode node){
       list.add(node);
       siftUp(size()-1);
     }
     
     public void siftUp(int index){
       int toSift = index;
       int parent = (toSift-1)/2;
       
       while(parent>=0 && list.get(toSift).getFrequency() > list.get(parent).getFrequency()){
         HuffmanNode save = list.get(toSift);
         list.set(toSift,list.get(parent));
         list.set(parent,save);
       }
      
       toSift = parent;
       parent = (toSift -1)/2;
     }
     
     public void siftDown(int index){
       int toSift = index;
       int child = 2*toSift +1;
       
       while(child<size()){
         if(child + 1< size() && list.get(child).getFrequency() < list.get(child+1).getFrequency())
           child = child +1;
         
         if(list.get(toSift).getFrequency()>= list.get(child).getFrequency())
           return;
         else{
           HuffmanNode save = list.get(toSift);
           list.set(toSift,list.get(child));
           list.set(child,save);
           
           toSift = child;
           child = 2*toSift +1;
         }
       } 
     }
     
     public void buildHeap(){
       for(int i=(size()-2)/2;i>=0;i--)
         siftDown(i);
     }
     
     public void heapSort(){
       buildHeap();
       
       ArrayList<HuffmanNode> newArray = new ArrayList<HuffmanNode>();
       int endUnsorted = size()-1;
       
       while(endUnsorted > 0){
         HuffmanNode toMove = list.get(0);
         newArray.add(0,toMove);
         list.set(0,list.get(endUnsorted));
         list.remove(endUnsorted);
         siftDown(0);
         endUnsorted--;
       }
       newArray.add(0,list.get(0));
       list = newArray;
     }
  }
    
  /* Scan the input text to generate the initial list of HuffmanNode
   */
  public void buildHuffmanList(File inputFile){
    try{
      Scanner sc = new Scanner(inputFile);
      while(sc.hasNextLine()){

        String line = sc.nextLine();
        for(int i =0;i<line.length();i++){
         
          if(freqTable.isEmpty())
            freqTable.put(line.charAt(i),1);
          else{
            if(freqTable.containsKey(line.charAt(i)) == false)
              freqTable.put(line.charAt(i),1);
            else{
              int oldValue = freqTable.get(line.charAt(i));
              freqTable.replace(line.charAt(i),oldValue+1);
            }
          }
        }
      }
    }
    catch(FileNotFoundException e){
      System.out.println("Can't find the file");
    }
  }
  
  /* This merges two huffmanNode
   */
  public HuffmanNode merge(HuffmanNode node1, HuffmanNode node2){
    int combinedFrequency = node1.getFrequency() + node2.getFrequency();
    HuffmanNode combinedNode = new HuffmanNode(null, combinedFrequency);
    combinedNode.setLeft(node1);
    combinedNode.setRight(node2);
    return combinedNode;
  }
  
  /* Produce the Huffman Tree
   */
  public void makeTree(){
    //convert Hashmap into charList
    for(Map.Entry<Character,Integer> entry : freqTable.entrySet()){
      HuffmanNode newNode = new HuffmanNode(entry.getKey(),entry.getValue());
      charList.add(newNode);
    }
    
    if(charList.size()==0)
      return;
    
    if(charList.size()==1){
      HuffmanNode onlyNode = charList.get(0);
      root = new HuffmanNode(null,onlyNode.getFrequency());
      root.setLeft(onlyNode);
      return;
    }
    
    Sort heap = new Sort(charList);
    heap.heapSort();
    
    while(heap.size()>1){
      
      HuffmanNode leftNode = heap.remove(0);
      HuffmanNode rightNode = heap.remove(0);
      
      HuffmanNode newNode = merge(leftNode,rightNode);
      heap.insert(newNode);
      heap.heapSort();
    }
  
    charList = heap.getList();
    root = charList.get(0);
  }
  
  /* Tranverse the Huffman tree to output the econding for each character
   */
  public void encodeTree(){
    StringBuilder builder = new StringBuilder();
    encodeTreeHelper(root,builder);
  }
  
  /* The helper method to encode tree and put the encoded code into the encodedTable
   */
  public void encodeTreeHelper(HuffmanNode node, StringBuilder builder){
    if(node.getLeft() == null && node.getRight()== null && !node.getCharAt().equals(null))
     encodedTable.put(node.getCharAt(),builder.toString());
   else{
   encodeTreeHelper(node.getLeft(), builder.append("0"));
   encodeTreeHelper(node.getRight(), builder.append("1"));
   }
   
   if(builder.length()>0)
   builder.deleteCharAt(builder.length()-1);
  }
  
  /* This scan the input text again and produce the encoded output file and caculate savings
   */
  public void computeSaving(File inputFile, File outputFile){
    clearFile(outputFile);
    
    int normBits =0;
    int huffBits =0;
    try{
      Scanner sc = new Scanner(inputFile);
      BufferedWriter output = new BufferedWriter(new FileWriter(outputFile,true));
      while(sc.hasNextLine()){
        String line = sc.nextLine();
     
        for(int i =0; i<line.length();i++){
          Character toCaculate = line.charAt(i);
          
          huffBits = encodedTable.get(toCaculate).length()*freqTable.get(toCaculate);
          normBits = 8*freqTable.get(toCaculate);
          savings = normBits - huffBits;
            
          String encoded = "";
          encoded = encodedTable.get(toCaculate);
          output.write(encoded);
        }
      }
    output.close();
    }
    catch(IOException e){
      System.out.println("IOException");
    }
  }
  
  /* This method print the encodedTable into a file
   */
  public void printEncodedTable(File encodedFile){
    clearFile(encodedFile);
    try{
      BufferedWriter output = new BufferedWriter(new FileWriter(encodedFile,true));
      for(Map.Entry<Character,String> entry : encodedTable.entrySet()){
        Character c = entry.getKey();
        if(c >= 32 && c < 127)
        output.write(entry.getKey()+": "+entry.getValue());
        else
        output.write(" [0x" + Integer.toOctalString(c) + "]"+": "+entry.getValue());
        output.write("\n");
      }
      output.close();
    }catch(IOException e){
      System.out.println("IOException");
    }
  }
  
  /* This method print the amout of savings into a file
   */
  public void printSaving(File savingsFile){
    clearFile(savingsFile);
    try{
      BufferedWriter output = new BufferedWriter(new FileWriter(savingsFile,true));
      output.write("Using HuffmanCompressor saves "+ ((Integer)(savings)).toString()+" "+"bits");
      output.close();
    }
    catch(IOException e){
      System.out.println("IOException");
    }
  }
  
  /* This helps clear the content of the file without deleting the file itself
   */
  public void clearFile(File file){
    try{
    PrintWriter writer = new PrintWriter(file);
    writer.close();
    }
    catch(IOException e){
      System.out.println("IOException");
    }
  }
  
  /* This is the main method that take in the name of inputFile and outputFile 
   */
  public static void main(String[] args){
    huffmanCoder(args[0],args[1]);
  }
  
  /* This is the class that run the whole program
   */
  public static String huffmanCoder(String inputFileName, String outputFileName){
    File inputFile = new File(inputFileName+".txt");
    File outputFile = new File(outputFileName+".txt");
    File encodedFile = new File("encodedTable.txt");
    File savingFile = new File("Saving.txt");
    HuffmanCompressor run = new HuffmanCompressor();
    
    run.buildHuffmanList(inputFile);
    run.makeTree();
    run.encodeTree();
    run.computeSaving(inputFile,outputFile);
    run.printEncodedTable(encodedFile);
    run.printSaving(savingFile);
    return "Done!";
  }
}