import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class extendibleHashing {
    public static void main(String[] args) {
        Directory directory = new Directory(1, 3);
        System.out.println(directory.buckets.length);
        directory.insert(16,"16");
        directory.insert(4,"4");
        directory.insert(6,"6");
        directory.display();
        directory.insert(22,"22");
        directory.display();
        directory.insert(24,"24");
        directory.insert(10,"10");
        directory.display();
        directory.insert(31,"31");
        directory.insert(7,"7");
        directory.insert(9,"9");
        directory.display();
        directory.insert(20,"20");
        directory.display();
        directory.insert(26,"26");
        directory.display();
    }
}

class Bucket{
    int depth,size;
    Map<Integer,String> values;

    Bucket(int depth,int size){
        values = new HashMap<>();
        this.depth=depth;
        this.size=size;
    }

    Map<Integer,String> copyValues(){
        return new HashMap<>(values);
    }

    void clear(){
        values.clear();
    }

    boolean insert(int key,String value){
        if(isFull())
            return false;
        values.put(key,value);
        return true;
    }

    String get(int key){
        return values.get(key);
    }

    boolean isFull(){
        return values.size()==size;
    }

    int increaseDepth(){
        depth++;
        return depth;
    }
}

class Directory{
    int global_depth, bucket_size;

    Bucket[] buckets;

    Directory(int depth,int bucket_size){
        global_depth=depth;
        this.bucket_size=bucket_size;
        buckets=new Bucket[1<<depth];
        Arrays.setAll(buckets,x -> new Bucket(depth,bucket_size));
    }

    int hash(int n){
        return n&((1<<global_depth)-1);
    }

    void insert(int key,String value){
        int bucket_no=hash(key);
        System.out.println(key+" "+ bucket_no +" "+buckets.length);
        boolean status = buckets[bucket_no].insert(key,value);
        if(!status){
            split(bucket_no);
            insert(key,value);
        }
    }

    String get(int key){
        int bucket_no = hash(key);
        return buckets[bucket_no].get(key);
    }

    void split(int bucket_no){
        //??????local_depth ??????bucket???local_depth????????????global_depth
        //??????????????????????????????bucket???local_depth???????????????????????????global_depth
        int local_depth = buckets[bucket_no].increaseDepth();
        if(local_depth>global_depth){
            grow();
        }
        int pair_index = pairIndex(bucket_no, local_depth);
        buckets[pair_index] = new Bucket(local_depth,bucket_size);
        //????????????buckets[bucket_no]??????values?????? ????????????
        Map<Integer, String> tmp = buckets[bucket_no].copyValues();
        buckets[bucket_no].clear();

        //???buckets[pair_index]?????????directory?????????
        //??????buckets[pair_index]?????????local_depth??????pair_index???key?????????bucket
        //???Directory???global_depth????????????local_depth
        //?????????????????????local_depth??????pair_index???hash??????
        //????????????????????????????????????buckets[pair_index]
        int index_diff = 1<<local_depth;
        int dir_size = 1<<global_depth;
        for(int i=pair_index-index_diff ; i>=0 ; i-=index_diff )
            buckets[i] = buckets[pair_index];
        for(int i=pair_index+index_diff ; i<dir_size ; i+=index_diff )
            buckets[i] = buckets[pair_index];
        //rehash??????????????????
        for(Integer key : tmp.keySet()){
            insert(key,tmp.get(key));
        }
    }

    void grow(){
        Bucket[] newBucket = new Bucket[buckets.length*2];
        //????????????????????????????????????????????????????????????????????????bucket
        //????????????????????????4{0->0,1->1,2->0,3->3} ????????????????????????4{0->0,1->1,2->0,3->3,4->0,5->1,6->0,7->3}
        for(int i=0;i<buckets.length*2;i++){
            newBucket[i]=buckets[i%buckets.length];
        }
        global_depth++;
        buckets=newBucket;
    }

    /*
        get the new splited bucket number
        for example
        the original bucket number id 101 the depth is 3
        then in order to splite,the depth crease to 4
        the new splited bucket number is low bit keep upchange
        and set the new high bit to 1
        become 1101
        use bucket_no^(1<<(depth-1)) to calculate
    */

    int pairIndex(int bucket_no,int depth){
        return bucket_no^(1<<(depth-1));
    }

    void display(){
        for(int i=0;i<buckets.length;i++){
            System.out.println(i+" "+buckets[i].values);
        }
        System.out.println();
    }
}
