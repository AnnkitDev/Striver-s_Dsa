package Sorting;



class MissingNumber {

    public static void main(String[] args) {
        int arr[] = {2,4,1,0};
        System.out.println(missingNumber(arr));
    }
    public static int missingNumber(int[] arr) {


        int i = 0;
        while(i < arr.length){
            int correctIdx = arr[i];


            if(arr[i] < arr.length && arr[i] != arr[correctIdx]){
                swap(arr, i, correctIdx);

            }else{
                i++;
            }
        }

        for(int index = 0; index < arr.length; index++){
            if(arr[index] != index){
                return index;
            }
        }
        return arr.length;

        
    }


   static  void swap(int[] arr, int first, int second){
        int temp = arr[first];
        arr[first] = arr[second];
        arr[second] = temp;
    }
}