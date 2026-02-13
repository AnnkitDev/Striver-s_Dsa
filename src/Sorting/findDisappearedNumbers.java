package Sorting;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class findDisappearedNumbers {

    public static void main(String[] args) {
        int[] arr = new int[] {4,3,2,7,8,2,3,1};
        System.out.println(findDisappearedNumbers(arr));

    }
    public static List<Integer> findDisappearedNumbers(int[] nums) {
        int i = 0;
        while(i < nums.length){
            int correctIdx = nums[i] - 1;
            if(nums[i] != nums[correctIdx]){
                swap(nums, i, correctIdx);

            }else{
                i++;
            }
        }

        List<Integer> list = new ArrayList<Integer>();

        for (int index = 0; index <nums.length ; index++) {
            if(nums[index] != index+1){
                list.add(index + 1);
            }

        }

        return list;


        
    }

    static void swap(int[] arr, int first, int second){
        int temp = arr[first];
        arr[first] = arr[second];
        arr[second] = temp;
    }
}