package Sorting;

class findDuplicate {

    public static void main(String[] args) {
        int[] nums = {1,3,4,2,2};
        System.out.println(findDuplicate(nums));
    }
    public static int findDuplicate(int[] nums) {
        int i = 0;
        while (i < nums.length){
            int correctIdx = nums[i] - 1;

            if(nums[i] != nums[correctIdx]){
                swap(nums, i, correctIdx);
            }else{
                i++;
            }
        }

        int index;

        for( index = 0; index < nums.length; index++){
            if(nums[index] != index+1){
                return nums[index];
            }
        }

        return nums[index];



        
        
    }

   static void swap(int[] nums, int first, int second){
        int temp = nums[first];
        nums[first] = nums[second];
        nums[second] = temp;
        
    }
}