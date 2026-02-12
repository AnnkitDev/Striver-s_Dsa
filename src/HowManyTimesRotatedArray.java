public class HowManyTimesRotatedArray {
    public static void main(String[] args) {
        int[] arr = {4,5,6,1,2,3};
        System.out.println(Solution(arr));

    }

    static int Solution(int[] nums){
        int start = 0;
        int end = nums.length - 1;

        while(start < end){
            int mid = start + (end - start) /2;


            if(nums[start] == nums[mid] && nums[mid] == nums[end]){
                start++;
                end--;
                continue;
            }

            if(nums[mid] > nums[end]){
                start = mid + 1;
            }else{
                end = mid;
            }
        }

        return start;
    }
}
