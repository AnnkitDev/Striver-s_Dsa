package Sorting;

import java.util.ArrayList;
import java.util.List;

class findDuplicatesInArray {
    public static void main(String[] args) {
        int[] nums = {4,3,2,7,8,2,3,1};
        System.out.println(findDuplicate(nums));
    }
    public static List<Integer> findDuplicate(int[] nums) {

        int i = 0;
        while (i < nums.length){
            int correctIdx = nums[i] - 1;

            if(nums[i] != nums[correctIdx]){
                swap(nums, i, correctIdx);
            }else{
                i++;
            }
        }

        List<Integer> list = new ArrayList<>();
        for(int index = 0; index < nums.length;index++){
            if(nums[index] != nums[index] + 1){
                list.add(nums[index]);

            }
        }

        return list;







    }

    static void swap(int[] nums, int first, int second){
        int temp = nums[first];
        nums[first] = nums[second];
        nums[second] = temp;

    }
}