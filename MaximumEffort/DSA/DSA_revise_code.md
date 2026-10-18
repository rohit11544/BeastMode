# DSA_revise_code — Optimal solutions

Daily pair with [`DSA_REVISE.md`](./DSA_REVISE.md). One clean optimal snippet per problem.  
Order matches `DSA_REVISE` (topic → pattern → problem).

---

## Arrays & Strings → Two Pointers

### Valid Palindrome

```java
class Solution {
    public boolean isPalindrome(String s) {
        int l = 0, r = s.length() - 1;

        while (l < r) {
            while (l < r && !isAlphaNumeric(s.charAt(l))) l++;
            while (l < r && !isAlphaNumeric(s.charAt(r))) r--;

            char cl = toLower(s.charAt(l));
            char cr = toLower(s.charAt(r));
            if (cl != cr) return false;

            l++;
            r--;
        }
        return true;
    }

    private boolean isAlphaNumeric(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
               (c >= '0' && c <= '9');
    }

    private char toLower(char c) {
        if (c >= 'A' && c <= 'Z') {
            return (char) (c + ('a' - 'A'));
        }
        return c;
    }
}
```

**Idea:** L/R scan; skip non-alphanumeric; compare lowercased chars.  
**T:** O(n) · **S:** O(1)

### Remove Duplicates (sorted)

```java
class Solution {
    public int removeDuplicates(int[] nums) {
        int n = nums.length;
        int slow = 0;
        for (int fast = 1; fast < n; fast++) {
            if (nums[slow] != nums[fast]) {
                nums[++slow] = nums[fast];
            }
        }
        return slow + 1;
    }
}
```

**Idea:** slow = last unique; fast scans; write new values at `++slow`.  
**T:** O(n) · **S:** O(1)

### Move Zeroes

```java
class Solution {
    public void moveZeroes(int[] nums) {
        int insertPos = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                int temp = nums[insertPos];
                nums[insertPos] = nums[i];
                nums[i] = temp;
                insertPos++;
            }
        }
    }
}
```

**Idea:** `insertPos` = next slot for a non-zero. Swap non-zeros forward; zeros bubble to the end.  
**T:** O(n) · **S:** O(1)

### Container With Most Water

```java
class Solution {
    public int maxArea(int[] height) {
        int r = height.length - 1;
        int l = 0;
        int ans = 0;
        while (l < r) {
            int length = Math.min(height[l], height[r]);
            int breadth = r - l;
            ans = Math.max(ans, length * breadth);
            if (height[l] < height[r]) l++;
            else r--;
        }
        return ans;
    }
}
```

**Idea:** L/R greedy — area = min(h)·width; move the shorter side inward.  
**T:** O(n) · **S:** O(1)

### 3Sum

```java
class Solution {
    public List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> ans = new ArrayList<>();
        int n = nums.length;

        for (int i = 0; i < n - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue; // skip duplicate first element

            int l = i + 1, r = n - 1;
            while (l < r) {
                int sum = nums[i] + nums[l] + nums[r];
                if (sum == 0) {
                    ans.add(Arrays.asList(nums[i], nums[l], nums[r]));
                    l++; r--;
                    while (l < r && nums[l] == nums[l - 1]) l++; // skip dup second element
                    while (l < r && nums[r] == nums[r + 1]) r--; // skip dup third element
                } else if (sum < 0) {
                    l++;
                } else {
                    r--;
                }
            }
        }
        return ans;
    }
}
```

**Idea:** sort + fix `i` + L/R two-sum; skip duplicates on all three positions.  
**T:** O(n²) · **S:** O(1) / O(log n) sort

### 4Sum

```java
class Solution {
    public List<List<Integer>> fourSum(int[] nums, int target) {
        Arrays.sort(nums);
        List<List<Integer>> ans = new ArrayList<>();
        int n = nums.length;

        for (int i = 0; i < n - 3; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue; // skip duplicate 1st element

            for (int j = i + 1; j < n - 2; j++) {
                if (j > i + 1 && nums[j] == nums[j - 1]) continue; // skip duplicate 2nd element

                int l = j + 1, r = n - 1;
                long remaining = (long) target - nums[i] - nums[j]; // avoid overflow

                while (l < r) {
                    long sum = (long) nums[l] + nums[r];
                    if (sum == remaining) {
                        ans.add(Arrays.asList(nums[i], nums[j], nums[l], nums[r]));
                        l++; r--;
                        while (l < r && nums[l] == nums[l - 1]) l++; // skip dup 3rd
                        while (l < r && nums[r] == nums[r + 1]) r--; // skip dup 4th
                    } else if (sum < remaining) {
                        l++;
                    } else {
                        r--;
                    }
                }
            }
        }
        return ans;
    }
}
```

**Idea:** sort + fix `i,j` + L/R on remaining; skip dups; `long` for overflow.  
**T:** O(n³) · **S:** O(1) / O(log n) sort

### Two Sum II (sorted)

```java
class Solution {
    public int[] twoSum(int[] numbers, int target) {
        int l = 0, r = numbers.length - 1;

        while (l < r) {
            int sum = numbers[l] + numbers[r];
            if (sum == target) {
                return new int[]{l + 1, r + 1}; // 1-indexed
            } else if (sum < target) {
                l++;
            } else {
                r--;
            }
        }
        return new int[]{-1, -1}; // unreachable per problem constraints (guaranteed one solution)
    }
}
```

**Idea:** L/R on sorted array; move L if sum too small, R if too big.  
**T:** O(n) · **S:** O(1)

### Trapping Rain Water (2ptr)

```java
class Solution {
    public int trap(int[] height) {
        int l = 0, r = height.length - 1;
        int leftMax = 0, rightMax = 0;
        int water = 0;

        while (l < r) {
            if (height[l] < height[r]) {
                leftMax = Math.max(leftMax, height[l]);
                water += leftMax - height[l];
                l++;
            } else {
                rightMax = Math.max(rightMax, height[r]);
                water += rightMax - height[r];
                r--;
            }
        }
        return water;
    }
}
```

**Idea:** L/R max — water at side limited by the smaller bounding max; advance the shorter side.  
**T:** O(n) · **S:** O(1)  
**Prefer this over mono-stack** (same time, better space).

### Longest Palindromic Substring

```java
class Solution {

    public String longestPalindrome(String s) {

        int start = 0;
        int end = 0;

        for (int i = 0; i < s.length(); i++) {

            int odd = expand(s, i, i);
            int even = expand(s, i, i + 1);

            int length = Math.max(odd, even);

            if (length > end - start + 1) {
                start = i - (length - 1) / 2;
                end = i + length / 2;
            }
        }

        return s.substring(start, end + 1);
    }

    private int expand(String s, int left, int right) {

        while (left >= 0 &&
               right < s.length() &&
               s.charAt(left) == s.charAt(right)) {

            left--;
            right++;
        }

        return right - left - 1;
    }
}
```

**Idea:** expand around each center (odd + even); track best `[start, end]`.  
**T:** O(n²) · **S:** O(1)

### Palindromic Substrings (count)

```java
class Solution {

    public int countSubstrings(String s) {

        int count = 0;

        for (int i = 0; i < s.length(); i++) {

            count += expand(s, i, i);       // odd-length
            count += expand(s, i, i + 1);   // even-length
        }

        return count;
    }

    private int expand(String s, int left, int right) {

        int count = 0;

        while (left >= 0 &&
               right < s.length() &&
               s.charAt(left) == s.charAt(right)) {

            count++;
            left--;
            right++;
        }

        return count;
    }
}
```

**Idea:** same expand centers; each successful expand step is one palindrome.  
**T:** O(n²) · **S:** O(1)

---

## Arrays & Strings → Sliding Window

### Max Sum Subarray Size K (distinct)

```java
class Solution {
    public long maximumSubarraySum(int[] nums, int k) {
        int n = nums.length;
        long curSum = 0;
        long ans = 0;
        Map<Integer, Integer> mp = new HashMap<>();
        for (int i = 0; i < k; i++) {
            mp.put(nums[i], mp.getOrDefault(nums[i], 0) + 1);
            curSum += nums[i];
        }
        if (mp.size() == k) ans = curSum;
        for (int i = k; i < n; i++) {
            mp.put(nums[i], mp.getOrDefault(nums[i], 0) + 1);
            curSum += nums[i];

            int val = mp.get(nums[i - k]);
            if (val == 1) mp.remove(nums[i - k]);
            else mp.put(nums[i - k], val - 1);
            curSum -= nums[i - k];

            if (mp.size() == k) {
                ans = Math.max(ans, curSum);
            }
        }

        return ans;
    }
}
```

**Idea:** fixed window of size k + freq map; update only when all elements distinct (`map.size() == k`).  
**T:** O(n) · **S:** O(k)

### Longest Substring Without Repeating

```java
class Solution {
    public int lengthOfLongestSubstring(String s) {
        int[] lastSeen = new int[128]; // ASCII range covers letters, digits, symbols, spaces
        Arrays.fill(lastSeen, -1);

        int left = 0, maxLen = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);

            if (lastSeen[c] >= left) {
                left = lastSeen[c] + 1; // jump left past the previous occurrence
            }

            lastSeen[c] = right;
            maxLen = Math.max(maxLen, right - left + 1);
        }

        return maxLen;
    }
}
```

**Idea:** expand/shrink — `lastSeen` index; jump `left` past prior duplicate.  
**T:** O(n) · **S:** O(1) (128)

### Longest Substring K Distinct

```java
class Solution {
    public int longestKSubstr(String s, int k) {
        int n = s.length();
        Map<Character, Integer> count = new HashMap<>();
        int left = 0, ans = -1;

        for (int right = 0; right < n; right++) {
            count.merge(s.charAt(right), 1, Integer::sum);

            while (count.size() > k) {
                char out = s.charAt(left);
                count.merge(out, -1, Integer::sum);
                if (count.get(out) == 0) count.remove(out);
                left++;
            }

            if (count.size() == k) {
                ans = Math.max(ans, right - left + 1);
            }
        }
        return ans;
    }
}
```

**Idea:** variable window — expand right; shrink while distinct `> k`; track max when size `== k`.  
**T:** O(n) · **S:** O(k)

### Minimum Window Substring

```java
class Solution {
    public String minWindow(String s, String t) {
        if (s.length() < t.length() || t.isEmpty()) return "";

        Map<Character, Integer> need = new HashMap<>();
        for (char c : t.toCharArray()) {
            need.merge(c, 1, Integer::sum);
        }

        Map<Character, Integer> window = new HashMap<>();
        int required = need.size();   // number of distinct chars in t that need satisfying
        int formed = 0;                // number of distinct chars currently satisfied

        int left = 0;
        int bestLen = Integer.MAX_VALUE, bestStart = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            window.merge(c, 1, Integer::sum);

            if (need.containsKey(c) && window.get(c).intValue() == need.get(c).intValue()) {
                formed++;
            }

            while (formed == required) {
                if (right - left + 1 < bestLen) {
                    bestLen = right - left + 1;
                    bestStart = left;
                }

                char out = s.charAt(left);
                window.put(out, window.get(out) - 1);
                if (need.containsKey(out) && window.get(out) < need.get(out)) {
                    formed--;
                }
                left++;
            }
        }

        return bestLen == Integer.MAX_VALUE ? "" : s.substring(bestStart, bestStart + bestLen);
    }
}
```

**Idea:** need/have counts — expand until valid (`formed == required`); shrink to minimize.  
**T:** O(|s| + |t|) · **S:** O(|Σ|) — maps hold distinct chars in `t` / window (≤ alphabet size; ASCII ≈ 128 → effectively O(1))

### Find All Anagrams

```java
class Solution {
    public List<Integer> findAnagrams(String s, String p) {
        List<Integer> ans = new ArrayList<>();
        int sn = s.length(), pn = p.length();
        if (pn > sn) return ans;

        int[] gt = new int[26];
        int[] curFreq = new int[26];
        for (char c : p.toCharArray()) gt[c - 'a']++;

        int matches = 0;
        for (int i = 0; i < 26; i++) {
            if (gt[i] == 0) matches++; // letters not in p trivially match (0 == 0) from the start
        }

        for (int i = 0; i < sn; i++) {
            int inIdx = s.charAt(i) - 'a';
            curFreq[inIdx]++;
            if (curFreq[inIdx] == gt[inIdx]) matches++;
            else if (curFreq[inIdx] == gt[inIdx] + 1) matches--;

            if (i >= pn) {
                int outIdx = s.charAt(i - pn) - 'a';
                curFreq[outIdx]--;
                if (curFreq[outIdx] == gt[outIdx]) matches++;
                else if (curFreq[outIdx] == gt[outIdx] - 1) matches--;
            }

            if (i >= pn - 1 && matches == 26) {
                ans.add(i - pn + 1);
            }
        }
        return ans;
    }
}
```

**Idea:** fixed window of len `|p|`; 26-letter freq match counter (`matches == 26`).  
**T:** O(|s|) · **S:** O(1) (26)

### Permutation in String

```java
class Solution {
    public boolean checkInclusion(String s1, String s2) {
        int n1 = s1.length(), n2 = s2.length();
        if (n1 > n2) return false;

        int[] need = new int[26];
        int[] window = new int[26];
        for (char c : s1.toCharArray()) need[c - 'a']++;

        int matches = 0;
        for (int i = 0; i < 26; i++) {
            if (need[i] == 0) matches++; // letters not in s1 trivially match (0 == 0)
        }

        for (int i = 0; i < n2; i++) {
            int inIdx = s2.charAt(i) - 'a';
            window[inIdx]++;
            if (window[inIdx] == need[inIdx]) matches++;
            else if (window[inIdx] == need[inIdx] + 1) matches--;

            if (i >= n1) {
                int outIdx = s2.charAt(i - n1) - 'a';
                window[outIdx]--;
                if (window[outIdx] == need[outIdx]) matches++;
                else if (window[outIdx] == need[outIdx] - 1) matches--;
            }

            if (i >= n1 - 1 && matches == 26) return true;
        }

        return false;
    }
}
```

**Idea:** same as Find All Anagrams — fixed window freq; return true on first `matches == 26`.  
**T:** O(|s2|) · **S:** O(1) (26)

### Count Anagram Occurrences

```java
class Solution {
    int search(String pat, String txt) {
        int n = txt.length(), m = pat.length();
        if (m > n) return 0;

        int[] need = new int[26];
        int[] window = new int[26];
        for (char c : pat.toCharArray()) need[c - 'a']++;

        int matches = 0;
        for (int i = 0; i < 26; i++) {
            if (need[i] == 0) matches++; // letters absent from pat trivially match at 0==0
        }

        int count = 0;
        for (int i = 0; i < n; i++) {
            int inIdx = txt.charAt(i) - 'a';
            window[inIdx]++;
            if (window[inIdx] == need[inIdx]) matches++;
            else if (window[inIdx] == need[inIdx] + 1) matches--;

            if (i >= m) {
                int outIdx = txt.charAt(i - m) - 'a';
                window[outIdx]--;
                if (window[outIdx] == need[outIdx]) matches++;
                else if (window[outIdx] == need[outIdx] - 1) matches--;
            }

            if (i >= m - 1 && matches == 26) count++;
        }

        return count;
    }
}
```

**Idea:** same fixed-window anagram match as Find All Anagrams; count hits instead of indices.  
**T:** O(|txt|) · **S:** O(1) (26)

---

## Arrays & Strings → Prefix Sum + HashMap

### Subarray Sum Equals K

```java
class Solution {
    public int subarraySum(int[] nums, int k) {
        Map<Integer, Integer> prefixCount = new HashMap<>();
        prefixCount.put(0, 1); // empty prefix sums to 0

        int sum = 0, count = 0;
        for (int num : nums) {
            sum += num;
            count += prefixCount.getOrDefault(sum - k, 0);
            prefixCount.merge(sum, 1, Integer::sum);
        }
        return count;
    }
}
```

**Idea:** prefix + map — at each `sum`, add count of prior prefixes equal to `sum - k`.  
**T:** O(n) · **S:** O(n)

### Subarray Sum 0

```java
class Solution {
    public boolean subArrayExists(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        seen.add(0); // empty prefix sums to 0
        int sum = 0;
        for (int num : nums) {
            sum += num;
            if (!seen.add(sum)) return true; // add() returns false if sum was already present
        }
        return false;
    }
}
```

**Idea:** prefix + set — repeat prefix ⇒ subarray sum 0 between them.  
**T:** O(n) · **S:** O(n)

### Contiguous Array (0/1)

```java
class Solution {
    public int findMaxLength(int[] nums) {
        Map<Integer, Integer> firstIndex = new HashMap<>();
        firstIndex.put(0, -1); // empty prefix (before index 0) sums to 0

        int sum = 0, ans = 0;
        for (int i = 0; i < nums.length; i++) {
            sum += (nums[i] == 0) ? -1 : 1;

            if (firstIndex.containsKey(sum)) {
                ans = Math.max(ans, i - firstIndex.get(sum));
            } else {
                firstIndex.put(sum, i);
            }
        }
        return ans;
    }
}
```

**Idea:** treat 0 as -1; same prefix balance ⇒ equal 0s/1s; keep first index of each sum.  
**T:** O(n) · **S:** O(n)

### Subarray Sums Divisible by K

```java
class Solution {
    public int subarraysDivByK(int[] nums, int k) {
        Map<Integer, Integer> mp = new HashMap<>();
        mp.put(0, 1);
        int sum = 0, ans = 0;

        for (int num : nums) {
            sum += num;
            int rem = ((sum % k) + k) % k;
            ans += mp.getOrDefault(rem, 0);
            mp.merge(rem, 1, Integer::sum);
        }
        return ans;
    }
}
```

**Idea:** same rem ⇒ subarray divisible by k; **normalize rem** `((sum % k) + k) % k` for negatives.  
**T:** O(n) · **S:** O(k)

### Range Sum Query (immutable)

```java
class NumArray {
    int[] arr;

    public NumArray(int[] nums) {
        int n = nums.length;
        arr = new int[n];
        arr[0] = nums[0];
        for (int i = 1; i < n; i++) {
            arr[i] = arr[i - 1] + nums[i];
        }
    }

    public int sumRange(int left, int right) {
        if (left > 0) return arr[right] - arr[left - 1];
        else return arr[right];
    }
}
```

**Idea:** build prefix once; range = `prefix[right] - prefix[left-1]`.  
**T:** O(n) build, O(1) query · **S:** O(n)

---

## Arrays & Strings → Hashing / Frequency

### Two Sum

```java
class Solution {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> seen = new HashMap<>(); // value -> index

        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (seen.containsKey(complement)) {
                return new int[]{seen.get(complement), i};
            }
            seen.put(nums[i], i);
        }

        return new int[]{}; // unreachable per problem guarantee (exactly one solution exists)
    }
}
```

**Idea:** hashmap value→index; look for `target - nums[i]` before inserting.  
**T:** O(n) · **S:** O(n)

### Group Anagrams

**Approach 1 — sort key (default)**

```java
class Solution {
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> groups = new HashMap<>();

        for (String s : strs) {
            char[] chars = s.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);

            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }

        return new ArrayList<>(groups.values());
    }
}
```

**T:** O(n · k log k) · **S:** O(n · k) — `n` = #strings, `k` = max length

**Approach 2 — count signature (no sort)**

```java
class Solution {
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> groups = new HashMap<>();

        for (String s : strs) {
            int[] count = new int[26];
            for (char c : s.toCharArray()) count[c - 'a']++;

            StringBuilder key = new StringBuilder();
            for (int c : count) {
                key.append(c).append('#'); // delimiter avoids ambiguity, e.g. [1,11] vs [11,1]
            }

            groups.computeIfAbsent(key.toString(), k -> new ArrayList<>()).add(s);
        }

        return new ArrayList<>(groups.values());
    }
}
```

**T:** O(n · k) · **S:** O(n · k) — drops the log k factor

**When to use which**
- **Approach 1 (sort)** — default; simplest to write/explain; fine for typical short strings  
- **Approach 2 (count)** — long strings, or asked to avoid O(k log k) sort

### Longest Consecutive Sequence

```java
class Solution {
    public int longestConsecutive(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int num : nums) set.add(num);

        int ans = 0;

        for (int num : nums) {
            if (!set.contains(num)) continue; // already consumed by an earlier expansion

            // only expand from true sequence starts
            if (set.contains(num - 1)) continue;

            int length = 1;
            int next = num + 1;
            while (set.contains(next)) {
                set.remove(next);
                length++;
                next++;
            }

            ans = Math.max(ans, length);
        }

        return ans;
    }
}
```

**Idea:** set scan — expand only from starts (`num-1` missing); remove visited to avoid rework.  
**T:** O(n) · **S:** O(n)

### Top K Frequent (bucket)

```java
class Solution {
    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int num : nums) freq.merge(num, 1, Integer::sum);

        List<List<Integer>> buckets = new ArrayList<>();
        for (int i = 0; i <= nums.length; i++) buckets.add(new ArrayList<>());

        for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
            buckets.get(entry.getValue()).add(entry.getKey());
        }

        int[] ans = new int[k];
        int idx = 0;
        for (int f = buckets.size() - 1; f >= 0 && idx < k; f--) {
            for (int num : buckets.get(f)) {
                ans[idx++] = num;
                if (idx == k) break;
            }
        }
        return ans;
    }
}
```

**Idea:** freq map → buckets by count; scan high→low for top k (linear, no heap).  
**T:** O(n) · **S:** O(n)  
**Alt:** heap of size k — O(n log k) — see Heap section.

---

## Arrays & Strings → Sorting + Logic

### Merge Intervals

```java
class Solution {
    public int[][] merge(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

        List<int[]> merged = new ArrayList<>();

        for (int[] interval : intervals) {
            if (merged.isEmpty() || merged.get(merged.size() - 1)[1] < interval[0]) {
                merged.add(interval);
            } else {
                merged.get(merged.size() - 1)[1] = Math.max(merged.get(merged.size() - 1)[1], interval[1]);
            }
        }

        return merged.toArray(new int[merged.size()][]);
    }
}
```

**Idea:** sort by start; merge if overlap with last, else append. Prefer `Integer.compare(a[0], b[0])` over `a[0]-b[0]`.  
**T:** O(n log n) · **S:** O(n)

### Insert Interval

```java
class Solution {
    public int[][] insert(int[][] intervals, int[] newInterval) {
        List<int[]> result = new ArrayList<>();
        int n = intervals.length;
        int i = 0;

        // 1. Add all intervals ending before newInterval starts (no overlap)
        while (i < n && intervals[i][1] < newInterval[0]) {
            result.add(intervals[i]);
            i++;
        }

        // 2. Merge all intervals that overlap with newInterval
        int start = newInterval[0], end = newInterval[1];
        while (i < n && intervals[i][0] <= end) {
            start = Math.min(start, intervals[i][0]);
            end = Math.max(end, intervals[i][1]);
            i++;
        }
        result.add(new int[]{start, end});

        // 3. Add all remaining intervals (start after merged interval ends)
        while (i < n) {
            result.add(intervals[i]);
            i++;
        }

        return result.toArray(new int[result.size()][]);
    }
}
```

**Idea:** scan/merge — before / overlap-merge / after (assumes intervals already sorted).  
**T:** O(n) · **S:** O(n)

### Meeting Rooms I

```java
class Solution {
    public boolean canAttendMeetings(int[][] arr) {
        Arrays.sort(arr, (a, b) -> a[0] - b[0]);

        for (int i = 1; i < arr.length; i++) {
            if (arr[i][0] < arr[i - 1][1]) {
                return false;
            }
        }
        return true;
    }
}
```

**Idea:** sort by start; any overlap ⇒ false. Prefer `Integer.compare(a[0], b[0])`.  
**T:** O(n log n) · **S:** O(1) / O(log n) sort

### Meeting Rooms II

```java
class Solution {
    public int minMeetingRooms(int[] start, int[] end) {
        int n = start.length;
        int[] starts = start.clone();
        int[] ends = end.clone();
        Arrays.sort(starts);
        Arrays.sort(ends);

        int rooms = 0, maxRooms = 0;
        int i = 0, j = 0;

        while (i < n) {
            if (starts[i] < ends[j]) {
                rooms++;
                i++;
            } else {
                rooms--;
                j++;
            }
            maxRooms = Math.max(maxRooms, rooms);
        }

        return maxRooms;
    }
}
```

**Idea:** sort starts & ends separately; 2ptr sweep — start before end ⇒ +room.  
**T:** O(n log n) · **S:** O(n)

### Non-overlapping Intervals

```java
class Solution {
    public int eraseOverlapIntervals(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> a[1] - b[1]); // sort by end time

        int count = 0;
        int lastEnd = Integer.MIN_VALUE;

        for (int[] interval : intervals) {
            if (interval[0] >= lastEnd) {
                lastEnd = interval[1]; // keep this interval
            } else {
                count++; // remove this interval (it overlaps with the kept one)
            }
        }
        return count;
    }
}
```

**Idea:** sort by end; greedy keep non-overlapping; count removals. Prefer `Integer.compare(a[1], b[1])`.  
**T:** O(n log n) · **S:** O(1) / O(log n) sort

### K Closest Points

```java
class Solution {
    public int[][] kClosest(int[][] points, int k) {
        PriorityQueue<int[]> pq = new PriorityQueue<>(
            (a, b) -> (b[0]*b[0] + b[1]*b[1]) - (a[0]*a[0] + a[1]*a[1])
        );

        for (int[] point : points) {
            pq.offer(point);
            if (pq.size() > k) pq.poll();
        }

        int[][] ans = new int[k][];
        for (int i = 0; i < k; i++) {
            ans[i] = pq.poll();
        }
        return ans;
    }
}
```

**Idea:** max-heap of size k by distance²; poll farthest when size > k.  
**T:** O(n log k) · **S:** O(k)

---

## Arrays & Strings → Binary Search

### First/Last Position

```java
class Solution {
    public int[] searchRange(int[] nums, int target) {
        int first = findBound(nums, target, true);
        if (first == -1) return new int[]{-1, -1};
        int last = findBound(nums, target, false);
        return new int[]{first, last};
    }

    private int findBound(int[] nums, int target, boolean findFirst) {
        int low = 0, high = nums.length - 1;
        int result = -1;

        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (nums[mid] == target) {
                result = mid;
                if (findFirst) {
                    high = mid - 1; // keep searching left for an earlier occurrence
                } else {
                    low = mid + 1;  // keep searching right for a later occurrence
                }
            } else if (nums[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return result;
    }
}
```

**Idea:** bisect twice — on hit, go left for first / right for last.  
**T:** O(log n) · **S:** O(1)

### Search Rotated Sorted Array

```java
class Solution {
    public int search(int[] nums, int target) {
        int n = nums.length;
        int rot = findRot(nums);
        int l = 0, r = n - 1;

        while (l <= r) {
            int mid = (l + r) / 2;
            int actual = (mid + rot) % n;
            if (nums[actual] == target) return actual;
            if (target > nums[actual]) {
                l = mid + 1;
            } else {
                r = mid - 1;
            }
        }
        return -1;
    }

    private int findRot(int[] nums) {
        int l = 0, r = nums.length - 1;
        while (l < r) {
            int mid = (l + r) / 2;
            if (nums[mid] > nums[r]) {
                l = mid + 1;
            } else {
                r = mid;
            }
        }
        return l; // pivot index — index of the minimum element
    }
}
```

**Idea:** find pivot (min), then BS with mapped index `(mid + rot) % n`.  
**T:** O(log n) · **S:** O(1)

### Find Min in Rotated

```java
class Solution {
    public int findMin(int[] nums) {
        int l = 0, r = nums.length - 1;
        while (l < r) {
            int mid = (l + r) / 2;
            if (nums[mid] > nums[r]) {
                l = mid + 1;
            } else {
                r = mid;
            }
        }
        return nums[l]; // pivot — minimum element
    }
}
```

**Idea:** bisect — if `mid > r`, min is in right half; else shrink right to mid.  
**T:** O(log n) · **S:** O(1)

### Koko Eating Bananas

```java
class Solution {

    public int minEatingSpeed(int[] piles, int h) {

        int left = 1;
        int right = 0;

        for (int pile : piles)
            right = Math.max(right, pile);

        while (left < right) {

            int mid = left + (right - left) / 2;

            if (canEat(piles, mid, h))
                right = mid;
            else
                left = mid + 1;
        }

        return left;
    }

    private boolean canEat(int[] piles, int k, int h) {

        long hours = 0;

        for (int pile : piles) {
            hours += (pile + k - 1) / k;

            if (hours > h)
                return false;
        }

        return true;
    }
}
```

**Idea:** BS on speed `k`; check hours with ceil `(pile+k-1)/k`.  
**T:** O(n log M) · **S:** O(1) — `M` = max pile

### Min Days for Bouquets

```java
class Solution {
    public int minDays(int[] bloomDay, int m, int k) {

        if ((long) m * k > bloomDay.length)
            return -1;

        int low = Integer.MAX_VALUE;
        int high = Integer.MIN_VALUE;

        for (int day : bloomDay) {
            low = Math.min(low, day);
            high = Math.max(high, day);
        }

        int ans = -1;

        while (low <= high) {

            int mid = low + (high - low) / 2;

            if (canMake(bloomDay, m, k, mid)) {
                ans = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        return ans;
    }

    private boolean canMake(int[] bloomDay, int m, int k, int day) {

        int flowers = 0;

        for (int bloom : bloomDay) {

            if (bloom <= day) {

                flowers++;

                if (flowers == k) {
                    m--;
                    flowers = 0;

                    if (m == 0)
                        return true;
                }

            } else {
                flowers = 0;
            }
        }

        return false;
    }
}
```

**Idea:** BS on day; greedy count adjacent groups of `k` bloomed flowers.  
**T:** O(n log M) · **S:** O(1) — `M` = max bloom day

### Capacity To Ship Packages

```java
class Solution {

    public int shipWithinDays(int[] weights, int days) {

        int low = 0;
        int high = 0;

        // Minimum possible capacity = max weight
        // Maximum possible capacity = sum of all weights
        for (int weight : weights) {
            low = Math.max(low, weight);
            high += weight;
        }

        while (low < high) {

            int mid = low + (high - low) / 2;

            if (canShip(weights, days, mid)) {
                high = mid;
            } else {
                low = mid + 1;
            }
        }

        return low;
    }

    private boolean canShip(int[] weights, int days, int capacity) {

        int daysNeeded = 1;
        int currentLoad = 0;

        for (int weight : weights) {

            if (currentLoad + weight > capacity) {
                daysNeeded++;
                currentLoad = 0;
            }

            currentLoad += weight;

            if (daysNeeded > days) {
                return false;
            }
        }

        return true;
    }
}
```

**Idea:** BS on capacity; greedy pack days; low = max weight, high = sum.  
**T:** O(n log S) · **S:** O(1) — `S` = sum of weights

### Split Array Largest Sum

```java
class Solution {

    public int splitArray(int[] nums, int k) {
        long low = 0;
        long high = 0;

        // Search space:
        // low  = largest element
        // high = sum of all elements
        for (int num : nums) {
            low = Math.max(low, num);
            high += num;
        }

        while (low < high) {
            long mid = low + (high - low) / 2;

            if (canSplit(nums, k, mid)) {
                high = mid;          // Try a smaller maximum sum
            } else {
                low = mid + 1;       // Need a larger maximum sum
            }
        }

        return (int) low;
    }

    // Returns true if nums can be split into at most k subarrays
    // such that each subarray sum is <= maxAllowedSum.
    private boolean canSplit(int[] nums, int k, long maxAllowedSum) {
        int subarrays = 1;
        long currentSum = 0;

        for (int num : nums) {
            if (currentSum + num <= maxAllowedSum) {
                currentSum += num;
            } else {
                subarrays++;
                currentSum = num;

                if (subarrays > k) {
                    return false;
                }
            }
        }

        return true;
    }
}
```

**Idea:** BS on max subarray sum; greedy split count ≤ k (same pattern as ship packages).  
**T:** O(n log S) · **S:** O(1) — `S` = sum of nums

### Median of Two Sorted Arrays

```java
class Solution {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {

        // Always binary search on the smaller array.
        if (nums1.length > nums2.length) {
            return findMedianSortedArrays(nums2, nums1);
        }

        int m = nums1.length;
        int n = nums2.length;

        int left = 0;
        int right = m;

        while (left <= right) {

            // Partition in nums1
            int partitionX = left + (right - left) / 2;

            // Partition in nums2 so that left half has (m+n+1)/2 elements
            int partitionY = (m + n + 1) / 2 - partitionX;

            int maxLeftX = (partitionX == 0) ? Integer.MIN_VALUE : nums1[partitionX - 1];
            int minRightX = (partitionX == m) ? Integer.MAX_VALUE : nums1[partitionX];

            int maxLeftY = (partitionY == 0) ? Integer.MIN_VALUE : nums2[partitionY - 1];
            int minRightY = (partitionY == n) ? Integer.MAX_VALUE : nums2[partitionY];

            // Correct partition
            if (maxLeftX <= minRightY && maxLeftY <= minRightX) {

                // Even total length
                if ((m + n) % 2 == 0) {
                    return (Math.max(maxLeftX, maxLeftY)
                          + Math.min(minRightX, minRightY)) / 2.0;
                }

                // Odd total length
                return Math.max(maxLeftX, maxLeftY);
            }

            // Too many elements taken from nums1
            else if (maxLeftX > minRightY) {
                right = partitionX - 1;
            }

            // Too few elements taken from nums1
            else {
                left = partitionX + 1;
            }
        }

        return 0;
    }
}
```

**Idea:** BS partition on smaller array; left halves max ≤ right halves min.  
**T:** O(log(min(m,n))) · **S:** O(1)

---

## Arrays & Strings → String Parsing / Simulation

### Valid Parentheses

```java
class Solution {
    public boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();

        for (char ch : s.toCharArray()) {

            if (ch == '(' || ch == '{' || ch == '[') {
                stack.push(ch);
            } else {
                if (stack.isEmpty()) {
                    return false;
                }

                char open = stack.pop();

                if ((ch == ')' && open != '(') ||
                    (ch == '}' && open != '{') ||
                    (ch == ']' && open != '[')) {
                    return false;
                }
            }
        }

        return stack.isEmpty();
    }
}
```

**Idea:** stack — push opens; pop and match closes; empty at end.  
**T:** O(n) · **S:** O(n)

### Decode String

```java
class Solution {
    public String decodeString(String s) {
        Deque<Integer> countStack = new ArrayDeque<>();
        Deque<StringBuilder> stringStack = new ArrayDeque<>();

        StringBuilder current = new StringBuilder();
        int number = 0;

        for (char ch : s.toCharArray()) {

            if (Character.isDigit(ch)) {
                number = number * 10 + (ch - '0');
            }

            else if (ch == '[') {
                countStack.push(number);
                stringStack.push(current);

                current = new StringBuilder();
                number = 0;
            }

            else if (ch == ']') {
                int repeat = countStack.pop();
                StringBuilder previous = stringStack.pop();

                for (int i = 0; i < repeat; i++) {
                    previous.append(current);
                }

                current = previous;
            }

            else {
                current.append(ch);
            }
        }

        return current.toString();
    }
}
```

**Idea:** two stacks (counts + strings); on `]` expand current into previous.  
**T:** O(n) · **S:** O(n) — output-bound

### String Compression

```java
class Solution {
    public int compress(char[] chars) {
        int write = 0;
        int read = 0;
        int n = chars.length;

        while (read < n) {
            char currentChar = chars[read];
            int count = 0;

            // Count consecutive occurrences of current character
            while (read < n && chars[read] == currentChar) {
                read++;
                count++;
            }

            // Write the character
            chars[write++] = currentChar;

            // Write the count if greater than 1
            if (count > 1) {
                String countString = String.valueOf(count);

                for (char digit : countString.toCharArray()) {
                    chars[write++] = digit;
                }
            }
        }

        return write;
    }
}
```

**Idea:** 2ptr write — group consecutive chars; write char + digits of count if > 1.  
**T:** O(n) · **S:** O(1)

### Compare Version Numbers

```java
class Solution {
    public int compareVersion(String v1, String v2) {
        int i = 0;
        int j = 0;

        int n1 = v1.length();
        int n2 = v2.length();

        while (i < n1 || j < n2) {

            int num1 = 0;
            int num2 = 0;

            // Parse next revision number from v1
            while (i < n1 && v1.charAt(i) != '.') {
                num1 = num1 * 10 + (v1.charAt(i) - '0');
                i++;
            }

            // Parse next revision number from v2
            while (j < n2 && v2.charAt(j) != '.') {
                num2 = num2 * 10 + (v2.charAt(j) - '0');
                j++;
            }

            if (num1 != num2) {
                return num1 > num2 ? 1 : -1;
            }

            // Move past '.'
            if (i < n1 && v1.charAt(i) == '.') {
                i++;
            }

            if (j < n2 && v2.charAt(j) == '.') {
                j++;
            }
        }

        return 0;
    }
}
```

**Idea:** scan/parse each revision as int (leading zeros ignored); compare pairwise.  
**T:** O(n) · **S:** O(1)

### Basic Calculator II

```java
class Solution {
    public int calculate(String s) {
        Deque<Integer> stack = new ArrayDeque<>();

        int num = 0;
        char operator = '+';

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);

            // Build the current number
            if (Character.isDigit(ch)) {
                num = num * 10 + (ch - '0');
            }

            // Process when we hit an operator or the end
            if ((!Character.isDigit(ch) && ch != ' ') || i == s.length() - 1) {

                if (operator == '+') {
                    stack.push(num);
                }
                else if (operator == '-') {
                    stack.push(-num);
                }
                else if (operator == '*') {
                    stack.push(stack.pop() * num);
                }
                else if (operator == '/') {
                    stack.push(stack.pop() / num);
                }

                operator = ch;
                num = 0;
            }
        }

        int result = 0;

        while (!stack.isEmpty()) {
            result += stack.pop();
        }

        return result;
    }
}
```

**Idea:** stack ops — `+/-` push; `*/` apply to top immediately; sum stack.  
**T:** O(n) · **S:** O(n)

---

## Arrays & Strings → Greedy on Strings

### Remove K Digits

```java
class Solution {
    public String removeKdigits(String num, int k) {

        Deque<Character> stack = new ArrayDeque<>();

        for (char digit : num.toCharArray()) {

            while (!stack.isEmpty()
                    && k > 0
                    && stack.peekLast() > digit) {

                stack.pollLast();
                k--;
            }

            stack.offerLast(digit);
        }

        // If removals are still left, remove from the end.
        while (k > 0) {
            stack.pollLast();
            k--;
        }

        // Build the answer while skipping leading zeros.
        StringBuilder result = new StringBuilder();
        boolean leadingZero = true;

        while (!stack.isEmpty()) {
            char digit = stack.pollFirst();

            if (leadingZero && digit == '0') {
                continue;
            }

            leadingZero = false;
            result.append(digit);
        }

        return result.length() == 0 ? "0" : result.toString();
    }
}
```

**Idea:** mono stack — pop larger digits when a smaller one arrives; strip leading zeros.  
**T:** O(n) · **S:** O(n)

### Partition Labels

```java
class Solution {
    public List<Integer> partitionLabels(String s) {
        int[] lastIndex = new int[26];

        // Store the last occurrence of every character.
        for (int i = 0; i < s.length(); i++) {
            lastIndex[s.charAt(i) - 'a'] = i;
        }

        List<Integer> result = new ArrayList<>();

        int start = 0;
        int end = 0;

        for (int i = 0; i < s.length(); i++) {

            // Extend the current partition if needed.
            end = Math.max(end, lastIndex[s.charAt(i) - 'a']);

            // Found the end of the current partition.
            if (i == end) {
                result.add(end - start + 1);
                start = i + 1;
            }
        }

        return result;
    }
}
```

**Idea:** last-index greedy — extend partition end; cut when `i == end`.  
**T:** O(n) · **S:** O(26)

### Reorganize String

```java
class Solution {

    private static class Pair {
        char ch;
        int freq;

        Pair(char ch, int freq) {
            this.ch = ch;
            this.freq = freq;
        }
    }

    public String reorganizeString(String s) {
        int[] freq = new int[26];

        for (char ch : s.toCharArray()) {
            freq[ch - 'a']++;
        }

        int maxFreq = 0;
        for (int count : freq) {
            maxFreq = Math.max(maxFreq, count);
        }

        // Impossible to rearrange
        if (maxFreq > (s.length() + 1) / 2) {
            return "";
        }

        PriorityQueue<Pair> maxHeap = new PriorityQueue<>(
            (a, b) -> b.freq - a.freq
        );

        for (int i = 0; i < 26; i++) {
            if (freq[i] > 0) {
                maxHeap.offer(new Pair((char) ('a' + i), freq[i]));
            }
        }

        StringBuilder result = new StringBuilder();
        Pair previous = null;

        while (!maxHeap.isEmpty()) {

            Pair current = maxHeap.poll();

            result.append(current.ch);
            current.freq--;

            // The previous character can now be used again.
            if (previous != null && previous.freq > 0) {
                maxHeap.offer(previous);
            }

            // Hold the current character until the next iteration.
            previous = current;
        }

        return result.toString();
    }
}
```

**Idea:** max-heap by freq; place one char, hold previous so no adjacent same.  
**T:** O(n log 26) · **S:** O(26)

### Smallest Subsequence / Remove Duplicate Letters

```java
class Solution {
    public String removeDuplicateLetters(String s) {
        int[] lastIndex = new int[26];

        // Store the last occurrence of every character.
        for (int i = 0; i < s.length(); i++) {
            lastIndex[s.charAt(i) - 'a'] = i;
        }

        Deque<Character> stack = new ArrayDeque<>();
        boolean[] visited = new boolean[26];

        for (int i = 0; i < s.length(); i++) {
            char current = s.charAt(i);

            // Skip if already included in the answer.
            if (visited[current - 'a']) {
                continue;
            }

            // Maintain lexicographical order.
            while (!stack.isEmpty()
                    && stack.peekLast() > current
                    && lastIndex[stack.peekLast() - 'a'] > i) {

                char removed = stack.pollLast();
                visited[removed - 'a'] = false;
            }

            stack.offerLast(current);
            visited[current - 'a'] = true;
        }

        StringBuilder result = new StringBuilder();

        while (!stack.isEmpty()) {
            result.append(stack.pollFirst());
        }

        return result.toString();
    }
}
```

**Idea:** mono stack + last index — pop if larger and still appears later; one of each char.  
**T:** O(n) · **S:** O(26)

---

## Arrays & Strings → Monotonic Stack

### Daily Temperatures

```java
class Solution {
    public int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] answer = new int[n];

        // Stores indices of days with decreasing temperatures
        Deque<Integer> stack = new ArrayDeque<>();

        for (int i = n - 1; i >= 0; i--) {

            // Remove days that are not warmer than today
            while (!stack.isEmpty() &&
                   temperatures[stack.peek()] <= temperatures[i]) {
                stack.pop();
            }

            // If stack is not empty, the top is the next warmer day
            if (!stack.isEmpty()) {
                answer[i] = stack.peek() - i;
            }

            // Add today's index for future days
            stack.push(i);
        }

        return answer;
    }
}
```

**Idea:** next greater (from right) — mono decreasing stack of indices.  
**T:** O(n) · **S:** O(n)

### Next Greater Element

```java
class Solution {
    public int[] nextGreaterElement(int[] nums1, int[] nums2) {

        Map<Integer, Integer> nextGreaterMap = new HashMap<>();
        Deque<Integer> stack = new ArrayDeque<>();

        // Build next greater element map for nums2
        for (int num : nums2) {

            while (!stack.isEmpty() && stack.peek() < num) {
                nextGreaterMap.put(stack.pop(), num);
            }

            stack.push(num);
        }

        // Remaining elements have no greater element
        while (!stack.isEmpty()) {
            nextGreaterMap.put(stack.pop(), -1);
        }

        // Build answer for nums1
        int[] answer = new int[nums1.length];

        for (int i = 0; i < nums1.length; i++) {
            answer[i] = nextGreaterMap.get(nums1[i]);
        }

        return answer;
    }
}
```

**Idea:** mono stack on `nums2` → map value→next greater; lookup for `nums1`.  
**T:** O(n) · **S:** O(n)

### Largest Rectangle in Histogram

```java
class Solution {
    public int largestRectangleArea(int[] heights) {
        Deque<Integer> stack = new ArrayDeque<>();
        int maxArea = 0;

        for (int i = 0; i <= heights.length; i++) {

            int currentHeight = (i == heights.length) ? 0 : heights[i];

            while (!stack.isEmpty() &&
                   heights[stack.peek()] > currentHeight) {

                int height = heights[stack.pop()];

                int leftBoundary = stack.isEmpty() ? -1 : stack.peek();

                int width = i - leftBoundary - 1;

                maxArea = Math.max(maxArea, height * width);
            }

            stack.push(i);
        }

        return maxArea;
    }
}
```

**Idea:** mono increasing stack of indices; pop → width to nearest smaller L/R.  
**T:** O(n) · **S:** O(n)

### Stock Span

```java
class StockSpanner {

    private static class Pair {
        int price;
        int span;

        Pair(int price, int span) {
            this.price = price;
            this.span = span;
        }
    }

    private Deque<Pair> stack;

    public StockSpanner() {
        stack = new ArrayDeque<>();
    }

    public int next(int price) {

        int span = 1;

        while (!stack.isEmpty() && stack.peek().price <= price) {
            span += stack.pop().span;
        }

        stack.push(new Pair(price, span));

        return span;
    }
}
```

**Idea:** mono stack of (price, span); pop ≤ price and sum spans. Amortized O(1)/call.  
**T:** O(1) amortized · **S:** O(n)

---

## Arrays & Strings → Matrix / 2D

### Rotate Image

```java
class Solution {
    public void rotate(int[][] matrix) {
        int n = matrix.length;

        // Step 1: Transpose the matrix
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }

        // Step 2: Reverse each row
        for (int i = 0; i < n; i++) {
            int left = 0;
            int right = n - 1;

            while (left < right) {
                int temp = matrix[i][left];
                matrix[i][left] = matrix[i][right];
                matrix[i][right] = temp;

                left++;
                right--;
            }
        }
    }
}
```

**Idea:** transpose + reverse each row (90° clockwise in-place).  
**T:** O(n²) · **S:** O(1)

### Set Matrix Zeroes

```java
class Solution {
    public void setZeroes(int[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;

        boolean firstRowZero = false;
        boolean firstColZero = false;

        // Check if first row contains zero
        for (int j = 0; j < n; j++) {
            if (matrix[0][j] == 0) {
                firstRowZero = true;
                break;
            }
        }

        // Check if first column contains zero
        for (int i = 0; i < m; i++) {
            if (matrix[i][0] == 0) {
                firstColZero = true;
                break;
            }
        }

        // Use first row and first column as markers
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {

                if (matrix[i][j] == 0) {
                    matrix[i][0] = 0;
                    matrix[0][j] = 0;
                }
            }
        }

        // Zero cells based on markers
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {

                if (matrix[i][0] == 0 || matrix[0][j] == 0) {
                    matrix[i][j] = 0;
                }
            }
        }

        // Zero first row if needed
        if (firstRowZero) {
            for (int j = 0; j < n; j++) {
                matrix[0][j] = 0;
            }
        }

        // Zero first column if needed
        if (firstColZero) {
            for (int i = 0; i < m; i++) {
                matrix[i][0] = 0;
            }
        }
    }
}
```

**Idea:** first row/col as markers; handle first row/col with flags at end.  
**T:** O(mn) · **S:** O(1)

### Spiral Matrix

```java
class Solution {
    public List<Integer> spiralOrder(int[][] matrix) {

        List<Integer> result = new ArrayList<>();

        int top = 0;
        int bottom = matrix.length - 1;
        int left = 0;
        int right = matrix[0].length - 1;

        while (top <= bottom && left <= right) {

            // Traverse left -> right
            for (int col = left; col <= right; col++) {
                result.add(matrix[top][col]);
            }
            top++;

            // Traverse top -> bottom
            for (int row = top; row <= bottom; row++) {
                result.add(matrix[row][right]);
            }
            right--;

            // Traverse right -> left
            if (top <= bottom) {
                for (int col = right; col >= left; col--) {
                    result.add(matrix[bottom][col]);
                }
                bottom--;
            }

            // Traverse bottom -> top
            if (left <= right) {
                for (int row = bottom; row >= top; row--) {
                    result.add(matrix[row][left]);
                }
                left++;
            }
        }

        return result;
    }
}
```

**Idea:** boundary walk — shrink top/bottom/left/right each layer.  
**T:** O(mn) · **S:** O(1) extra (output O(mn))

### Search 2D Matrix

```java
class Solution {
    public boolean searchMatrix(int[][] matrix, int target) {

        int rows = matrix.length;
        int cols = matrix[0].length;

        int left = 0;
        int right = rows * cols - 1;

        while (left <= right) {

            int mid = left + (right - left) / 2;

            int row = mid / cols;
            int col = mid % cols;

            int value = matrix[row][col];

            if (value == target) {
                return true;
            }
            else if (value < target) {
                left = mid + 1;
            }
            else {
                right = mid - 1;
            }
        }

        return false;
    }
}
```

**Idea:** treat as sorted 1D — `row = mid/cols`, `col = mid%cols`.  
**T:** O(log(mn)) · **S:** O(1)

### Search 2D Matrix II

```java
class Solution {
    public boolean searchMatrix(int[][] matrix, int target) {

        int rows = matrix.length;
        int cols = matrix[0].length;

        int row = 0;
        int col = cols - 1;

        while (row < rows && col >= 0) {

            int current = matrix[row][col];

            if (current == target) {
                return true;
            }

            if (current > target) {
                // Eliminate this column
                col--;
            } else {
                // Eliminate this row
                row++;
            }
        }

        return false;
    }
}
```

**Idea:** staircase from top-right — larger → down, smaller → left.  
**T:** O(m+n) · **S:** O(1)

---

## Graphs → BFS

### Rotting Oranges

```java
class Solution {
    public int orangesRotting(int[][] grid) {

        int rows = grid.length;
        int cols = grid[0].length;

        Queue<int[]> queue = new ArrayDeque<>();

        int freshCount = 0;

        // Initialize queue with all rotten oranges
        // Count fresh oranges
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                if (grid[r][c] == 2) {
                    queue.offer(new int[]{r, c});
                }
                else if (grid[r][c] == 1) {
                    freshCount++;
                }
            }
        }

        if (freshCount == 0) {
            return 0;
        }

        int minutes = 0;

        int[][] directions = {
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1}
        };

        // Multi-source BFS
        while (!queue.isEmpty() && freshCount > 0) {

            int size = queue.size();

            for (int i = 0; i < size; i++) {

                int[] current = queue.poll();

                int r = current[0];
                int c = current[1];

                for (int[] dir : directions) {

                    int nr = r + dir[0];
                    int nc = c + dir[1];

                    // Check boundaries and fresh orange
                    if (nr >= 0 && nr < rows &&
                        nc >= 0 && nc < cols &&
                        grid[nr][nc] == 1) {

                        grid[nr][nc] = 2;
                        freshCount--;

                        queue.offer(new int[]{nr, nc});
                    }
                }
            }

            minutes++;
        }

        return freshCount == 0 ? minutes : -1;
    }
}
```

**Idea:** multi-source BFS — all rotten at once; level = minute; fail if fresh remain.  
**T:** O(mn) · **S:** O(mn)

### Shortest Path in Binary Matrix

```java
class Solution {
    public int shortestPathBinaryMatrix(int[][] grid) {

        int n = grid.length;

        // Start or end blocked
        if (grid[0][0] != 0 || grid[n - 1][n - 1] != 0) {
            return -1;
        }

        Queue<int[]> queue = new ArrayDeque<>();

        queue.offer(new int[]{0, 0});

        // Mark visited
        grid[0][0] = 1;

        int pathLength = 1;

        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
        };

        while (!queue.isEmpty()) {

            int size = queue.size();

            // Process one BFS level
            for (int i = 0; i < size; i++) {

                int[] current = queue.poll();

                int r = current[0];
                int c = current[1];

                // Destination reached
                if (r == n - 1 && c == n - 1) {
                    return pathLength;
                }

                for (int[] dir : directions) {

                    int nr = r + dir[0];
                    int nc = c + dir[1];

                    if (nr >= 0 && nr < n &&
                        nc >= 0 && nc < n &&
                        grid[nr][nc] == 0) {

                        grid[nr][nc] = 1; // mark visited
                        queue.offer(new int[]{nr, nc});
                    }
                }
            }

            pathLength++;
        }

        return -1;
    }
}
```

**Idea:** grid BFS (8-dir); level = path length; mark visited in-place.  
**T:** O(n²) · **S:** O(n²)

### Open the Lock

```java
class Solution {
    public int openLock(String[] deadends, String target) {

        Set<String> dead = new HashSet<>();

        for (String d : deadends) {
            dead.add(d);
        }

        String start = "0000";

        // Starting position is blocked
        if (dead.contains(start)) {
            return -1;
        }

        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();

        queue.offer(start);
        visited.add(start);

        int moves = 0;

        while (!queue.isEmpty()) {

            int size = queue.size();

            // Process one BFS level
            for (int i = 0; i < size; i++) {

                String current = queue.poll();

                if (current.equals(target)) {
                    return moves;
                }

                for (String next : getNeighbors(current)) {

                    if (!dead.contains(next) &&
                        !visited.contains(next)) {

                        visited.add(next);
                        queue.offer(next);
                    }
                }
            }

            moves++;
        }

        return -1;
    }


    private List<String> getNeighbors(String current) {

        List<String> neighbors = new ArrayList<>();

        char[] chars = current.toCharArray();

        for (int i = 0; i < 4; i++) {

            char original = chars[i];

            // Turn wheel forward
            chars[i] = original == '9' ? '0' : (char)(original + 1);
            neighbors.add(new String(chars));

            // Turn wheel backward
            chars[i] = original == '0' ? '9' : (char)(original - 1);
            neighbors.add(new String(chars));

            // Restore
            chars[i] = original;
        }

        return neighbors;
    }
}
```

**Idea:** BFS on state space (4 wheels × ±1); skip deadends/visited.  
**T:** O(10⁴) · **S:** O(10⁴)

### Word Ladder

```java
class Solution {
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {

        Set<String> words = new HashSet<>(wordList);

        // End word must exist
        if (!words.contains(endWord)) {
            return 0;
        }

        Queue<String> queue = new ArrayDeque<>();

        queue.offer(beginWord);

        int level = 1;

        while (!queue.isEmpty()) {

            int size = queue.size();

            for (int i = 0; i < size; i++) {

                String current = queue.poll();

                if (current.equals(endWord)) {
                    return level;
                }

                char[] chars = current.toCharArray();

                for (int pos = 0; pos < chars.length; pos++) {

                    char original = chars[pos];

                    for (char c = 'a'; c <= 'z'; c++) {

                        if (c == original) {
                            continue;
                        }

                        chars[pos] = c;

                        String next = new String(chars);

                        if (words.contains(next)) {

                            queue.offer(next);

                            // Mark visited
                            words.remove(next);
                        }
                    }

                    // Restore
                    chars[pos] = original;
                }
            }

            level++;
        }

        return 0;
    }
}
```

**Idea:** BFS on implicit graph — 1-letter neighbors; remove from set = visited.  
**T:** O(N · L · 26) · **S:** O(N · L)

### Walls and Gates

```java
class Solution {

    private static final int INF = Integer.MAX_VALUE;

    public void islandsAndTreasure(int[][] grid) {

        int rows = grid.length;
        int cols = grid[0].length;

        Queue<int[]> queue = new ArrayDeque<>();

        // Add all treasure cells to the queue
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                if (grid[r][c] == 0) {
                    queue.offer(new int[]{r, c});
                }
            }
        }

        int[][] directions = {
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1}
        };

        while (!queue.isEmpty()) {

            int[] current = queue.poll();

            int row = current[0];
            int col = current[1];

            for (int[] dir : directions) {

                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow < 0 || newRow >= rows ||
                    newCol < 0 || newCol >= cols ||
                    grid[newRow][newCol] != INF) {
                    continue;
                }

                grid[newRow][newCol] = grid[row][col] + 1;
                queue.offer(new int[]{newRow, newCol});
            }
        }
    }
}
```

**Idea:** multi-source BFS from all gates (0); fill INF rooms with distance.  
**T:** O(mn) · **S:** O(mn)

### Clone Graph

```java
class Solution {

    public Node cloneGraph(Node node) {

        if (node == null) {
            return null;
        }

        Map<Node, Node> cloneMap = new HashMap<>();
        Queue<Node> queue = new ArrayDeque<>();

        Node clone = new Node(node.val);

        cloneMap.put(node, clone);
        queue.offer(node);

        while (!queue.isEmpty()) {

            Node current = queue.poll();

            for (Node neighbor : current.neighbors) {

                if (!cloneMap.containsKey(neighbor)) {
                    cloneMap.put(neighbor, new Node(neighbor.val));
                    queue.offer(neighbor);
                }

                cloneMap.get(current).neighbors.add(cloneMap.get(neighbor));
            }
        }

        return clone;
    }
}
```

**Idea:** BFS + map old→new; clone neighbors as you visit.  
**T:** O(V+E) · **S:** O(V)

### Find if Path Exists

```java
class Solution {

    public boolean validPath(
        int n,
        int[][] edges,
        int source,
        int destination
    ) {

        if (source == destination) {
            return true;
        }


        // Build graph
        List<List<Integer>> graph = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }


        for (int[] edge : edges) {

            int a = edge[0];
            int b = edge[1];

            graph.get(a).add(b);
            graph.get(b).add(a);
        }


        boolean[] visited = new boolean[n];


        Queue<Integer> queue = new ArrayDeque<>();

        queue.offer(source);
        visited[source] = true;


        while (!queue.isEmpty()) {

            int current = queue.poll();


            if (current == destination) {
                return true;
            }


            for (int neighbor : graph.get(current)) {

                if (!visited[neighbor]) {

                    visited[neighbor] = true;

                    queue.offer(neighbor);
                }
            }
        }


        return false;
    }
}
```

**Idea:** BFS from source; true if destination reached.  
**T:** O(V+E) · **S:** O(V)

---

## Graphs → DFS

### Max Area of Island

```java
class Solution {

    private static final int[][] DIRECTIONS = {
        {1, 0},
        {-1, 0},
        {0, 1},
        {0, -1}
    };

    public int maxAreaOfIsland(int[][] grid) {

        int rows = grid.length;
        int cols = grid[0].length;

        int maxArea = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                if (grid[r][c] == 1) {
                    maxArea = Math.max(maxArea, dfs(grid, r, c));
                }
            }
        }

        return maxArea;
    }

    private int dfs(int[][] grid, int row, int col) {

        if (row < 0 || row >= grid.length ||
            col < 0 || col >= grid[0].length ||
            grid[row][col] == 0) {
            return 0;
        }

        // Mark visited
        grid[row][col] = 0;

        int area = 1;

        for (int[] dir : DIRECTIONS) {
            area += dfs(grid, row + dir[0], col + dir[1]);
        }

        return area;
    }
}
```

**Idea:** DFS flood — mark visited in-place; return component size.  
**T:** O(mn) · **S:** O(mn) recursion

### Pacific Atlantic Water Flow

```java
class Solution {

    private static final int[][] DIRS = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    public List<List<Integer>> pacificAtlantic(int[][] heights) {

        int rows = heights.length;
        int cols = heights[0].length;

        boolean[][] pacific = new boolean[rows][cols];
        boolean[][] atlantic = new boolean[rows][cols];

        // Pacific (top row + left column)
        for (int r = 0; r < rows; r++) {
            dfs(heights, r, 0, pacific);
            dfs(heights, r, cols - 1, atlantic);
        }

        for (int c = 0; c < cols; c++) {
            dfs(heights, 0, c, pacific);
            dfs(heights, rows - 1, c, atlantic);
        }

        List<List<Integer>> result = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                if (pacific[r][c] && atlantic[r][c]) {
                    result.add(Arrays.asList(r, c));
                }
            }
        }

        return result;
    }

    private void dfs(int[][] heights,
                     int row,
                     int col,
                     boolean[][] visited) {

        if (visited[row][col]) {
            return;
        }

        visited[row][col] = true;

        for (int[] dir : DIRS) {

            int nr = row + dir[0];
            int nc = col + dir[1];

            if (nr < 0 || nr >= heights.length ||
                nc < 0 || nc >= heights[0].length) {
                continue;
            }

            // Reverse flow: only go uphill or equal
            if (heights[nr][nc] >= heights[row][col]) {
                dfs(heights, nr, nc, visited);
            }
        }
    }
}
```

**Idea:** multi-source DFS from both oceans (reverse uphill); intersect reachable sets.  
**T:** O(mn) · **S:** O(mn)

---

## Graphs → Union Find (DSU)

### Number of Provinces

```java
class Solution {

    private int[] parent;
    private int[] rank;

    public int findCircleNum(int[][] isConnected) {

        int n = isConnected.length;

        parent = new int[n];
        rank = new int[n];

        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }

        int components = n;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {

                if (isConnected[i][j] == 1) {
                    if (union(i, j)) {
                        components--;
                    }
                }
            }
        }

        return components;
    }

    private int find(int x) {

        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }

        return parent[x];
    }

    private boolean union(int x, int y) {

        int rootX = find(x);
        int rootY = find(y);

        if (rootX == rootY) {
            return false;
        }

        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }

        return true;
    }
}
```

**Idea:** DSU — each city a component; union edges; decrement on merge.  
**T:** O(n² α(n)) · **S:** O(n)

### Count Complete Components

```java
class Solution {

    private int[] parent;
    private int[] rank;

    public int countCompleteComponents(int n, int[][] edges) {

        parent = new int[n];
        rank = new int[n];

        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }

        // Build connected components
        for (int[] edge : edges) {
            union(edge[0], edge[1]);
        }

        // Count nodes in each component
        Map<Integer, Integer> componentSize = new HashMap<>();

        for (int i = 0; i < n; i++) {
            int root = find(i);
            componentSize.put(root, componentSize.getOrDefault(root, 0) + 1);
        }

        // Count edges in each component
        Map<Integer, Integer> edgeCount = new HashMap<>();

        for (int[] edge : edges) {
            int root = find(edge[0]);
            edgeCount.put(root, edgeCount.getOrDefault(root, 0) + 1);
        }

        int completeComponents = 0;

        for (int root : componentSize.keySet()) {

            int nodes = componentSize.get(root);
            int edgesInComponent = edgeCount.getOrDefault(root, 0);

            if (edgesInComponent == nodes * (nodes - 1) / 2) {
                completeComponents++;
            }
        }

        return completeComponents;
    }

    private int find(int x) {

        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }

        return parent[x];
    }

    private void union(int x, int y) {

        int rootX = find(x);
        int rootY = find(y);

        if (rootX == rootY) {
            return;
        }

        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
    }
}
```

**Idea:** DSU components; complete iff edges == `n(n-1)/2` for that component.  
**T:** O((n+E) α(n)) · **S:** O(n)

### Redundant Connection

```java
class Solution {

    private int[] parent;
    private int[] rank;

    public int[] findRedundantConnection(int[][] edges) {

        int n = edges.length;

        parent = new int[n + 1];
        rank = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            parent[i] = i;
        }

        for (int[] edge : edges) {

            if (!union(edge[0], edge[1])) {
                return edge;
            }
        }

        return new int[0];
    }

    private int find(int x) {

        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }

        return parent[x];
    }

    private boolean union(int x, int y) {

        int rootX = find(x);
        int rootY = find(y);

        if (rootX == rootY) {
            return false;   // Cycle detected
        }

        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }

        return true;
    }
}
```

**Idea:** DSU cycle detect — first edge whose ends already connected is the answer.  
**T:** O(n α(n)) · **S:** O(n)

### Accounts Merge

```java
class Solution {

    private int[] parent;
    private int[] rank;

    public List<List<String>> accountsMerge(List<List<String>> accounts) {

        int n = accounts.size();

        parent = new int[n];
        rank = new int[n];

        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }

        Map<String, Integer> emailToAccount = new HashMap<>();

        // Union accounts sharing an email
        for (int i = 0; i < n; i++) {

            List<String> account = accounts.get(i);

            for (int j = 1; j < account.size(); j++) {

                String email = account.get(j);

                if (!emailToAccount.containsKey(email)) {
                    emailToAccount.put(email, i);
                } else {
                    union(i, emailToAccount.get(email));
                }
            }
        }

        // Group emails by root account
        Map<Integer, List<String>> rootToEmails = new HashMap<>();

        for (Map.Entry<String, Integer> entry : emailToAccount.entrySet()) {

            String email = entry.getKey();
            int root = find(entry.getValue());

            rootToEmails
                    .computeIfAbsent(root, k -> new ArrayList<>())
                    .add(email);
        }

        List<List<String>> result = new ArrayList<>();

        for (Map.Entry<Integer, List<String>> entry : rootToEmails.entrySet()) {

            List<String> emails = entry.getValue();
            Collections.sort(emails);

            List<String> merged = new ArrayList<>();
            merged.add(accounts.get(entry.getKey()).get(0)); // Name
            merged.addAll(emails);

            result.add(merged);
        }

        return result;
    }

    private int find(int x) {

        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }

        return parent[x];
    }

    private void union(int x, int y) {

        int rootX = find(x);
        int rootY = find(y);

        if (rootX == rootY) {
            return;
        }

        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
    }
}
```

**Idea:** DSU on accounts; union when email shared; group/sort emails by root.  
**T:** O(N·K log N) · **S:** O(N·K)

### Number of Islands II

```java
class Solution {

    private int[] parent;
    private int[] rank;

    public List<Integer> numOfIslands(int n, int m, int[][] operators) {

        parent = new int[n * m];
        rank = new int[n * m];

        for (int i = 0; i < n * m; i++) {
            parent[i] = i;
        }

        boolean[][] land = new boolean[n][m];

        List<Integer> answer = new ArrayList<>();

        int islands = 0;

        int[][] directions = {
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1}
        };

        for (int[] op : operators) {

            int row = op[0];
            int col = op[1];

            // Duplicate operation
            if (land[row][col]) {
                answer.add(islands);
                continue;
            }

            land[row][col] = true;
            islands++;

            int currentId = row * m + col;

            for (int[] dir : directions) {

                int nr = row + dir[0];
                int nc = col + dir[1];

                if (nr < 0 || nr >= n ||
                    nc < 0 || nc >= m ||
                    !land[nr][nc]) {
                    continue;
                }

                int neighborId = nr * m + nc;

                if (union(currentId, neighborId)) {
                    islands--;
                }
            }

            answer.add(islands);
        }

        return answer;
    }

    private int find(int x) {

        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }

        return parent[x];
    }

    private boolean union(int x, int y) {

        int rootX = find(x);
        int rootY = find(y);

        if (rootX == rootY) {
            return false;
        }

        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }

        return true;
    }
}
```

**Idea:** online DSU — add land, +1 island; union neighbors and −1 per merge.  
**T:** O(k α(mn)) · **S:** O(mn)

---

## Graphs → Topological Sort

### Course Schedule I

```java
class Solution {

    public boolean canFinish(int numCourses, int[][] prerequisites) {

        List<List<Integer>> graph = new ArrayList<>();

        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }

        int[] indegree = new int[numCourses];

        // Build graph and indegree array
        for (int[] prerequisite : prerequisites) {
            int course = prerequisite[0];
            int pre = prerequisite[1];

            graph.get(pre).add(course);
            indegree[course]++;
        }

        Queue<Integer> queue = new ArrayDeque<>();

        // Add all courses with indegree 0
        for (int course = 0; course < numCourses; course++) {
            if (indegree[course] == 0) {
                queue.offer(course);
            }
        }

        int completedCourses = 0;

        while (!queue.isEmpty()) {

            int current = queue.poll();
            completedCourses++;

            for (int neighbor : graph.get(current)) {

                indegree[neighbor]--;

                if (indegree[neighbor] == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        return completedCourses == numCourses;
    }
}
```

**Idea:** Kahn BFS — peel indegree 0; cycle if not all courses completed.  
**T:** O(V+E) · **S:** O(V+E)

### Course Schedule II

```java
class Solution {

    public int[] findOrder(int numCourses, int[][] prerequisites) {

        List<List<Integer>> graph = new ArrayList<>();

        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }

        int[] indegree = new int[numCourses];

        // Build graph
        for (int[] prerequisite : prerequisites) {
            int course = prerequisite[0];
            int pre = prerequisite[1];

            graph.get(pre).add(course);
            indegree[course]++;
        }

        Queue<Integer> queue = new ArrayDeque<>();

        // Add all courses with indegree 0
        for (int course = 0; course < numCourses; course++) {
            if (indegree[course] == 0) {
                queue.offer(course);
            }
        }

        int[] order = new int[numCourses];
        int index = 0;

        while (!queue.isEmpty()) {

            int current = queue.poll();
            order[index++] = current;

            for (int neighbor : graph.get(current)) {

                indegree[neighbor]--;

                if (indegree[neighbor] == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        // Cycle detected
        if (index != numCourses) {
            return new int[0];
        }

        return order;
    }
}
```

**Idea:** same Kahn as I — record order; empty array if cycle.  
**T:** O(V+E) · **S:** O(V+E)

### Alien Dictionary

```java
class Solution {

    public String findOrder(String[] words) {

        // Create graph for 26 lowercase letters
        List<List<Integer>> graph = new ArrayList<>();

        for (int i = 0; i < 26; i++) {
            graph.add(new ArrayList<>());
        }

        int[] indegree = new int[26];
        boolean[] exists = new boolean[26];

        int totalCharacters = 0;

        // Mark all characters that appear
        for (String word : words) {
            for (char ch : word.toCharArray()) {
                if (!exists[ch - 'a']) {
                    exists[ch - 'a'] = true;
                    totalCharacters++;
                }
            }
        }


        // Build graph by comparing adjacent words
        for (int i = 0; i < words.length - 1; i++) {

            String word1 = words[i];
            String word2 = words[i + 1];

            int len = Math.min(word1.length(), word2.length());

            boolean foundDifference = false;

            for (int j = 0; j < len; j++) {

                char ch1 = word1.charAt(j);
                char ch2 = word2.charAt(j);

                if (ch1 != ch2) {

                    graph.get(ch1 - 'a').add(ch2 - 'a');
                    indegree[ch2 - 'a']++;

                    foundDifference = true;
                    break;
                }
            }


            // Invalid case:
            // "abcd" cannot come before "ab"
            if (!foundDifference && word1.length() > word2.length()) {
                return "";
            }
        }


        // Kahn's Algorithm
        Queue<Integer> queue = new ArrayDeque<>();

        for (int i = 0; i < 26; i++) {
            if (exists[i] && indegree[i] == 0) {
                queue.offer(i);
            }
        }


        StringBuilder order = new StringBuilder();

        int count = 0;

        while (!queue.isEmpty()) {

            int current = queue.poll();

            order.append((char) (current + 'a'));
            count++;

            for (int neighbor : graph.get(current)) {

                indegree[neighbor]--;

                if (indegree[neighbor] == 0) {
                    queue.offer(neighbor);
                }
            }
        }


        // Cycle detection
        if (count != totalCharacters) {
            return "";
        }


        return order.toString();
    }
}
```

**Idea:** compare adjacent words → edges; Kahn on letters that exist; `""` if invalid/cycle.  
**T:** O(C) · **S:** O(1) alphabet (26)

### Parallel Courses

```java
class Solution {

    public int minimumSemesters(int n, int[][] relations) {

        // Build graph
        List<List<Integer>> graph = new ArrayList<>();

        for (int i = 0; i <= n; i++) {
            graph.add(new ArrayList<>());
        }

        int[] indegree = new int[n + 1];


        // Add edges and calculate indegree
        for (int[] relation : relations) {

            int prev = relation[0];
            int next = relation[1];

            graph.get(prev).add(next);
            indegree[next]++;
        }


        // Add all courses with indegree 0
        Queue<Integer> queue = new ArrayDeque<>();

        for (int course = 1; course <= n; course++) {

            if (indegree[course] == 0) {
                queue.offer(course);
            }
        }


        int semesters = 0;
        int completedCourses = 0;


        // Kahn's Algorithm level by level
        while (!queue.isEmpty()) {

            int size = queue.size();

            semesters++;


            // Courses available in this semester
            for (int i = 0; i < size; i++) {

                int current = queue.poll();

                completedCourses++;


                for (int next : graph.get(current)) {

                    indegree[next]--;

                    if (indegree[next] == 0) {
                        queue.offer(next);
                    }
                }
            }
        }


        // Cycle detected
        if (completedCourses != n) {
            return -1;
        }


        return semesters;
    }
}
```

**Idea:** Kahn by levels — each BFS level = one semester; −1 if cycle.  
**T:** O(V+E) · **S:** O(V+E)

### Minimum Height Trees

```java
class Solution {

    public List<Integer> findMinHeightTrees(int n, int[][] edges) {

        List<Integer> result = new ArrayList<>();

        // Edge case
        if (n == 1) {
            result.add(0);
            return result;
        }


        // Build graph
        List<List<Integer>> graph = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }


        int[] degree = new int[n];


        for (int[] edge : edges) {

            int a = edge[0];
            int b = edge[1];

            graph.get(a).add(b);
            graph.get(b).add(a);

            degree[a]++;
            degree[b]++;
        }


        Queue<Integer> queue = new ArrayDeque<>();


        // Add initial leaves
        for (int i = 0; i < n; i++) {

            if (degree[i] == 1) {
                queue.offer(i);
            }
        }


        int remainingNodes = n;


        // Remove leaves layer by layer
        while (remainingNodes > 2) {

            int size = queue.size();

            remainingNodes -= size;


            for (int i = 0; i < size; i++) {

                int leaf = queue.poll();


                for (int neighbor : graph.get(leaf)) {

                    degree[neighbor]--;

                    if (degree[neighbor] == 1) {
                        queue.offer(neighbor);
                    }
                }
            }
        }


        // Remaining nodes are centers
        while (!queue.isEmpty()) {
            result.add(queue.poll());
        }


        return result;
    }
}
```

**Idea:** peel leaves (degree 1) until ≤2 nodes left — those are MHT roots.  
**T:** O(V+E) · **S:** O(V)

---

## Graphs → Dijkstra

### Network Delay Time

```java
class Solution {

    public int networkDelayTime(int[][] times, int n, int k) {

        // Build graph
        List<List<int[]>> graph = new ArrayList<>();

        for (int i = 0; i <= n; i++) {
            graph.add(new ArrayList<>());
        }


        for (int[] time : times) {

            int from = time[0];
            int to = time[1];
            int weight = time[2];

            graph.get(from).add(new int[]{to, weight});
        }


        // Distance array
        int[] distance = new int[n + 1];

        Arrays.fill(distance, Integer.MAX_VALUE);


        distance[k] = 0;


        // Min heap: {node, current distance}
        PriorityQueue<int[]> pq = new PriorityQueue<>(
            (a, b) -> a[1] - b[1]
        );


        pq.offer(new int[]{k, 0});


        while (!pq.isEmpty()) {

            int[] current = pq.poll();

            int node = current[0];
            int time = current[1];


            // Ignore outdated entries
            if (time > distance[node]) {
                continue;
            }


            for (int[] neighbor : graph.get(node)) {

                int nextNode = neighbor[0];
                int weight = neighbor[1];


                int newTime = time + weight;


                if (newTime < distance[nextNode]) {

                    distance[nextNode] = newTime;

                    pq.offer(new int[]{nextNode, newTime});
                }
            }
        }


        // Find maximum shortest distance
        int answer = 0;


        for (int i = 1; i <= n; i++) {

            if (distance[i] == Integer.MAX_VALUE) {
                return -1;
            }

            answer = Math.max(answer, distance[i]);
        }


        return answer;
    }
}
```

**Idea:** Dijkstra from `k`; answer = max dist; −1 if any unreachable.  
**T:** O((V+E) log V) · **S:** O(V+E)

### Cheapest Flights Within K Stops

```java
class Solution {

    public int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {

        // Build graph
        List<List<int[]>> graph = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }


        for (int[] flight : flights) {

            int from = flight[0];
            int to = flight[1];
            int price = flight[2];

            graph.get(from).add(new int[]{to, price});
        }


        // {cost, city, stops}
        PriorityQueue<int[]> pq = new PriorityQueue<>(
            (a, b) -> a[0] - b[0]
        );


        pq.offer(new int[]{0, src, 0});


        // Minimum cost found with certain stops
        int[][] visited = new int[n][k + 2];

        for (int[] row : visited) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }

        visited[src][0] = 0;


        while (!pq.isEmpty()) {

            int[] current = pq.poll();

            int cost = current[0];
            int city = current[1];
            int stops = current[2];


            if (city == dst) {
                return cost;
            }


            if (stops > k) {
                continue;
            }


            for (int[] next : graph.get(city)) {

                int nextCity = next[0];
                int price = next[1];

                int newCost = cost + price;
                int newStops = stops + 1;


                if (newStops <= k + 1 &&
                    newCost < visited[nextCity][newStops]) {


                    visited[nextCity][newStops] = newCost;

                    pq.offer(new int[]{
                        newCost,
                        nextCity,
                        newStops
                    });
                }
            }
        }


        return -1;
    }
}
```

**Idea:** Dijkstra with state `(city, stops)`; trade stops vs cost; ≤ k stops.  
**T:** O(E·K log(V·K)) · **S:** O(V·K)

### Path with Maximum Probability

```java
class Solution {

    public double maxProbability(
        int n,
        int[][] edges,
        double[] succProb,
        int start,
        int end
    ) {

        // Build graph
        List<List<double[]>> graph = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }


        for (int i = 0; i < edges.length; i++) {

            int a = edges[i][0];
            int b = edges[i][1];
            double prob = succProb[i];


            graph.get(a).add(new double[]{b, prob});
            graph.get(b).add(new double[]{a, prob});
        }


        // Max heap
        PriorityQueue<double[]> pq =
            new PriorityQueue<>(
                (a, b) -> Double.compare(b[1], a[1])
            );


        double[] probability = new double[n];

        probability[start] = 1.0;


        pq.offer(new double[]{start, 1.0});


        while (!pq.isEmpty()) {

            double[] current = pq.poll();

            int node = (int) current[0];
            double prob = current[1];


            if (node == end) {
                return prob;
            }


            // Ignore outdated entry
            if (prob < probability[node]) {
                continue;
            }


            for (double[] neighbor : graph.get(node)) {

                int next = (int) neighbor[0];
                double edgeProb = neighbor[1];


                double newProb = prob * edgeProb;


                if (newProb > probability[next]) {

                    probability[next] = newProb;

                    pq.offer(new double[]{
                        next,
                        newProb
                    });
                }
            }
        }


        return 0.0;
    }
}
```

**Idea:** max-heap Dijkstra — multiply probs; maximize path product.  
**T:** O((V+E) log V) · **S:** O(V+E)

### Path With Minimum Effort

```java
class Solution {

    public int minimumEffortPath(int[][] heights) {

        int rows = heights.length;
        int cols = heights[0].length;


        int[][] effort = new int[rows][cols];

        for (int[] row : effort) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }


        PriorityQueue<int[]> pq =
            new PriorityQueue<>(
                (a, b) -> a[0] - b[0]
            );


        // {effort, row, col}
        effort[0][0] = 0;

        pq.offer(new int[]{0, 0, 0});


        int[][] directions = {
            {1,0},
            {-1,0},
            {0,1},
            {0,-1}
        };


        while (!pq.isEmpty()) {

            int[] current = pq.poll();

            int currEffort = current[0];
            int r = current[1];
            int c = current[2];


            // Reached destination
            if (r == rows - 1 && c == cols - 1) {
                return currEffort;
            }


            // Ignore outdated values
            if (currEffort > effort[r][c]) {
                continue;
            }


            for (int[] dir : directions) {

                int nr = r + dir[0];
                int nc = c + dir[1];


                if (nr < 0 || nr >= rows ||
                    nc < 0 || nc >= cols) {
                    continue;
                }


                int difference =
                    Math.abs(
                        heights[r][c] -
                        heights[nr][nc]
                    );


                int newEffort =
                    Math.max(currEffort, difference);


                if (newEffort < effort[nr][nc]) {

                    effort[nr][nc] = newEffort;

                    pq.offer(new int[]{
                        newEffort,
                        nr,
                        nc
                    });
                }
            }
        }


        return 0;
    }
}
```

**Idea:** Dijkstra — cost = max edge diff on path; minimize that max.  
**Note:** minimax — *minimum maximum edge difference*  
**T:** O(mn log(mn)) · **S:** O(mn)

### Swim in Rising Water

```java
class Solution {

    public int swimInWater(int[][] grid) {

        int n = grid.length;


        int[][] time = new int[n][n];

        for (int[] row : time) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }


        PriorityQueue<int[]> pq =
            new PriorityQueue<>(
                (a, b) -> a[0] - b[0]
            );


        // {time, row, col}
        time[0][0] = grid[0][0];

        pq.offer(new int[]{
            grid[0][0],
            0,
            0
        });


        int[][] directions = {
            {1,0},
            {-1,0},
            {0,1},
            {0,-1}
        };


        while (!pq.isEmpty()) {

            int[] current = pq.poll();

            int currTime = current[0];
            int r = current[1];
            int c = current[2];


            if (r == n - 1 && c == n - 1) {
                return currTime;
            }


            if (currTime > time[r][c]) {
                continue;
            }


            for (int[] dir : directions) {

                int nr = r + dir[0];
                int nc = c + dir[1];


                if (nr < 0 || nr >= n ||
                    nc < 0 || nc >= n) {
                    continue;
                }


                int newTime =
                    Math.max(
                        currTime,
                        grid[nr][nc]
                    );


                if (newTime < time[nr][nc]) {

                    time[nr][nc] = newTime;

                    pq.offer(new int[]{
                        newTime,
                        nr,
                        nc
                    });
                }
            }
        }


        return -1;
    }
}
```

**Idea:** Dijkstra — cost = max height on path; minimize that max (wait time).  
**Note:** minimax — *minimum maximum elevation encountered on the path*  
**T:** O(n² log n) · **S:** O(n²)

---

## Trees → Binary Tree DFS

### Max Depth

```java
class Solution {

    public int maxDepth(TreeNode root) {

        if (root == null) {
            return 0;
        }

        return 1 + Math.max(
            maxDepth(root.left),
            maxDepth(root.right)
        );
    }
}
```

**Idea:** DFS — 1 + max(left, right); null → 0.  
**T:** O(n) · **S:** O(h)

### Balanced Binary Tree

```java
class Solution {

    public boolean isBalanced(TreeNode root) {

        return checkHeight(root) != -1;
    }


    private int checkHeight(TreeNode node) {

        if (node == null) {
            return 0;
        }


        int leftHeight = checkHeight(node.left);

        if (leftHeight == -1) {
            return -1;
        }


        int rightHeight = checkHeight(node.right);

        if (rightHeight == -1) {
            return -1;
        }


        // Check balance condition
        if (Math.abs(leftHeight - rightHeight) > 1) {
            return -1;
        }


        // Return height
        return 1 + Math.max(leftHeight, rightHeight);
    }
}
```

**Idea:** DFS height — return −1 if |L−R| > 1 (early prune).  
**T:** O(n) · **S:** O(h)

### Symmetric Tree

```java
class Solution {

    public boolean isSymmetric(TreeNode root) {

        if (root == null) {
            return true;
        }

        return isMirror(root.left, root.right);
    }


    private boolean isMirror(TreeNode left, TreeNode right) {

        // Both empty
        if (left == null && right == null) {
            return true;
        }


        // One is empty, one is not
        if (left == null || right == null) {
            return false;
        }


        // Values must match and children must be mirrors
        return left.val == right.val
                && isMirror(left.left, right.right)
                && isMirror(left.right, right.left);
    }
}
```

**Idea:** mirror DFS — compare outer/inner pairs.  
**T:** O(n) · **S:** O(h)

### Subtree of Another Tree

```java
class Solution {

    public boolean isSubtree(TreeNode root, TreeNode subRoot) {

        if (root == null) {
            return false;
        }


        if (isSameTree(root, subRoot)) {
            return true;
        }


        return isSubtree(root.left, subRoot)
                || isSubtree(root.right, subRoot);
    }


    private boolean isSameTree(TreeNode p, TreeNode q) {

        if (p == null && q == null) {
            return true;
        }


        if (p == null || q == null) {
            return false;
        }


        if (p.val != q.val) {
            return false;
        }


        return isSameTree(p.left, q.left)
                && isSameTree(p.right, q.right);
    }
}
```

**Idea:** match DFS — at each node try `isSameTree`; else recurse L/R.  
**T:** O(n·m) · **S:** O(h)

### Path Sum

```java
class Solution {

    public boolean hasPathSum(TreeNode root, int targetSum) {

        if (root == null) {
            return false;
        }

        // Leaf node
        if (root.left == null && root.right == null) {
            return targetSum == root.val;
        }

        return hasPathSum(root.left, targetSum - root.val) ||
               hasPathSum(root.right, targetSum - root.val);
    }
}
```

**Idea:** DFS backtrack — subtract val; at leaf check remaining == val.  
**T:** O(n) · **S:** O(h)

### Path Sum II

```java
class Solution {

    public List<List<Integer>> pathSum(TreeNode root, int targetSum) {

        List<List<Integer>> result = new ArrayList<>();

        dfs(root, targetSum, new ArrayList<>(), result);

        return result;
    }


    private void dfs(
        TreeNode node,
        int remaining,
        List<Integer> path,
        List<List<Integer>> result
    ) {

        if (node == null) {
            return;
        }


        path.add(node.val);


        // Leaf node
        if (node.left == null && node.right == null) {

            if (remaining == node.val) {
                result.add(new ArrayList<>(path));
            }

        } else {

            dfs(node.left, remaining - node.val, path, result);

            dfs(node.right, remaining - node.val, path, result);
        }


        // Backtrack
        path.remove(path.size() - 1);
    }
}
```

**Idea:** DFS backtrack — collect path; copy to result at valid leaf.  
**T:** O(n) · **S:** O(h) (+ paths output)

### Lowest Common Ancestor (BT)

```java
class Solution {

    public TreeNode lowestCommonAncestor(
        TreeNode root,
        TreeNode p,
        TreeNode q
    ) {

        if (root == null || root == p || root == q) {
            return root;
        }


        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);


        // p and q found in different subtrees
        if (left != null && right != null) {
            return root;
        }


        // Both are in one subtree
        return left != null ? left : right;
    }
}
```

**Idea:** DFS — if L and R both non-null, root is LCA; else return the non-null side.  
**T:** O(n) · **S:** O(h)

### Serialize / Deserialize BT

```java
public class Codec {

    private static final String NULL = "N";
    private static final String SEPARATOR = ",";

    // Encodes a tree to a single string.
    public String serialize(TreeNode root) {

        StringBuilder sb = new StringBuilder();

        preorder(root, sb);

        return sb.toString();
    }


    private void preorder(TreeNode node, StringBuilder sb) {

        if (node == null) {
            sb.append(NULL).append(SEPARATOR);
            return;
        }

        sb.append(node.val).append(SEPARATOR);

        preorder(node.left, sb);
        preorder(node.right, sb);
    }


    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {

        String[] values = data.split(SEPARATOR);

        Queue<String> queue = new ArrayDeque<>(
            Arrays.asList(values)
        );

        return buildTree(queue);
    }


    private TreeNode buildTree(Queue<String> queue) {

        String value = queue.poll();

        if (value.equals(NULL)) {
            return null;
        }

        TreeNode node = new TreeNode(Integer.parseInt(value));

        node.left = buildTree(queue);
        node.right = buildTree(queue);

        return node;
    }
}
```

**Idea:** preorder DFS with null markers; rebuild by consuming queue in same order.  
**T:** O(n) · **S:** O(n)

---

## Trees → Binary Tree BFS

### Level Order Traversal

```java
class Solution {

    public List<List<Integer>> levelOrder(TreeNode root) {

        List<List<Integer>> result = new ArrayList<>();

        if (root == null) {
            return result;
        }

        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);

        while (!queue.isEmpty()) {

            int levelSize = queue.size();

            List<Integer> level = new ArrayList<>(levelSize);

            for (int i = 0; i < levelSize; i++) {

                TreeNode current = queue.poll();

                level.add(current.val);

                if (current.left != null) {
                    queue.offer(current.left);
                }

                if (current.right != null) {
                    queue.offer(current.right);
                }
            }

            result.add(level);
        }

        return result;
    }
}
```

**Idea:** BFS queue — process `levelSize` nodes per level.  
**T:** O(n) · **S:** O(n)

### Binary Tree Right Side View

```java
class Solution {

    public List<Integer> rightSideView(TreeNode root) {

        List<Integer> result = new ArrayList<>();

        if (root == null) {
            return result;
        }

        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);

        while (!queue.isEmpty()) {

            int levelSize = queue.size();

            for (int i = 0; i < levelSize; i++) {

                TreeNode current = queue.poll();

                if (current.left != null) {
                    queue.offer(current.left);
                }

                if (current.right != null) {
                    queue.offer(current.right);
                }

                // Last node of this level
                if (i == levelSize - 1) {
                    result.add(current.val);
                }
            }
        }

        return result;
    }
}
```

**Idea:** level BFS — take last node of each level.  
**T:** O(n) · **S:** O(n)

### Zigzag Level Order

```java
class Solution {

    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {

        List<List<Integer>> result = new ArrayList<>();

        if (root == null) {
            return result;
        }

        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);

        boolean leftToRight = true;

        while (!queue.isEmpty()) {

            int levelSize = queue.size();

            LinkedList<Integer> level = new LinkedList<>();

            for (int i = 0; i < levelSize; i++) {

                TreeNode current = queue.poll();

                if (leftToRight) {
                    level.addLast(current.val);
                } else {
                    level.addFirst(current.val);
                }

                if (current.left != null) {
                    queue.offer(current.left);
                }

                if (current.right != null) {
                    queue.offer(current.right);
                }
            }

            result.add(level);

            leftToRight = !leftToRight;
        }

        return result;
    }
}
```

**Idea:** BFS + deque — alternate `addLast` / `addFirst` per level.  
**T:** O(n) · **S:** O(n)

### Average of Levels

```java
class Solution {

    public List<Double> averageOfLevels(TreeNode root) {

        List<Double> result = new ArrayList<>();

        if (root == null) {
            return result;
        }

        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);

        while (!queue.isEmpty()) {

            int levelSize = queue.size();
            long levelSum = 0;

            for (int i = 0; i < levelSize; i++) {

                TreeNode current = queue.poll();

                levelSum += current.val;

                if (current.left != null) {
                    queue.offer(current.left);
                }

                if (current.right != null) {
                    queue.offer(current.right);
                }
            }

            result.add((double) levelSum / levelSize);
        }

        return result;
    }
}
```

**Idea:** BFS — sum each level / size (`long` avoids overflow).  
**T:** O(n) · **S:** O(n)

---

## Trees → BST

### Validate BST

```java
class Solution {

    public boolean isValidBST(TreeNode root) {

        return validate(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }


    private boolean validate(TreeNode node, long min, long max) {

        if (node == null) {
            return true;
        }


        if (node.val <= min || node.val >= max) {
            return false;
        }


        return validate(node.left, min, node.val)
                && validate(node.right, node.val, max);
    }
}
```

**Idea:** bounds DFS — left < val < right; use `long` for INT_MIN/MAX edge.  
**T:** O(n) · **S:** O(h)

### LCA of BST

```java
class Solution {

    public TreeNode lowestCommonAncestor(
        TreeNode root,
        TreeNode p,
        TreeNode q
    ) {

        if (p.val < root.val && q.val < root.val) {
            return lowestCommonAncestor(root.left, p, q);
        }

        if (p.val > root.val && q.val > root.val) {
            return lowestCommonAncestor(root.right, p, q);
        }

        return root;
    }
}
```

**Idea:** walk compare — both left / both right / else split → root.  
**T:** O(h) · **S:** O(h) recursion (O(1) iterative)

### Search BST

```java
class Solution {

    public TreeNode searchBST(TreeNode root, int val) {

        if (root == null || root.val == val) {
            return root;
        }

        if (val < root.val) {
            return searchBST(root.left, val);
        }

        return searchBST(root.right, val);
    }
}
```

**Idea:** walk — left if smaller, right if larger.  
**T:** O(h) · **S:** O(h)

### Insert into BST

```java
class Solution {

    public TreeNode insertIntoBST(TreeNode root, int val) {

        if (root == null) {
            return new TreeNode(val);
        }

        if (val < root.val) {
            root.left = insertIntoBST(root.left, val);
        } else {
            root.right = insertIntoBST(root.right, val);
        }

        return root;
    }
}
```

**Idea:** walk to null leaf slot; attach new node.  
**T:** O(h) · **S:** O(h)

### Delete Node in BST

```java
class Solution {

    public TreeNode deleteNode(TreeNode root, int key) {

        if (root == null) {
            return null;
        }

        if (key < root.val) {

            root.left = deleteNode(root.left, key);

        } else if (key > root.val) {

            root.right = deleteNode(root.right, key);

        } else {

            // Case 1 & 2
            if (root.left == null) {
                return root.right;
            }

            if (root.right == null) {
                return root.left;
            }

            // Case 3
            TreeNode successor = findMin(root.right);

            root.val = successor.val;

            root.right = deleteNode(root.right, successor.val);
        }

        return root;
    }


    private TreeNode findMin(TreeNode node) {

        while (node.left != null) {
            node = node.left;
        }

        return node;
    }
}
```

**Idea:** 0/1 child → replace; 2 children → copy inorder successor, delete successor.  
**T:** O(h) · **S:** O(h)

### Kth Smallest in BST

```java
class Solution {

    private int count = 0;

    public int kthSmallest(TreeNode root, int k) {

        return inorder(root, k);
    }


    private int inorder(TreeNode node, int k) {

        if (node == null) {
            return -1;
        }


        int left = inorder(node.left, k);

        if (left != -1) {
            return left;
        }


        count++;

        if (count == k) {
            return node.val;
        }


        return inorder(node.right, k);
    }
}
```

**Idea:** inorder (sorted order); return when count hits k.  
**T:** O(h+k) · **S:** O(h)

**Follow-up (augmented BST):** store `subtreeSize` on each node. Then:
- if `k == leftSize + 1` → current is answer  
- if `k <= leftSize` → go left  
- else → go right for `k - leftSize - 1`  

→ **O(h)** per query (no full inorder).

### Convert Sorted Array → BST

```java
class Solution {

    public TreeNode sortedArrayToBST(int[] nums) {

        return build(nums, 0, nums.length - 1);
    }


    private TreeNode build(int[] nums, int left, int right) {

        if (left > right) {
            return null;
        }


        int mid = left + (right - left) / 2;


        TreeNode root = new TreeNode(nums[mid]);


        root.left = build(nums, left, mid - 1);

        root.right = build(nums, mid + 1, right);


        return root;
    }
}
```

**Idea:** mid as root → balanced; recurse left/right halves.  
**T:** O(n) · **S:** O(log n)

### Min Absolute Diff in BST

```java
class Solution {

    private int minDiff = Integer.MAX_VALUE;
    private TreeNode prev = null;


    public int getMinimumDifference(TreeNode root) {

        inorder(root);

        return minDiff;
    }


    private void inorder(TreeNode node) {

        if (node == null) {
            return;
        }


        // Left subtree
        inorder(node.left);


        // Process current node
        if (prev != null) {
            minDiff = Math.min(
                minDiff,
                node.val - prev.val
            );
        }

        prev = node;


        // Right subtree
        inorder(node.right);
    }
}
```

**Idea:** inorder adjacent pairs — min diff is between consecutive nodes.  
**T:** O(n) · **S:** O(h)

---

## Trees → Heap / Priority Queue

### Kth Largest Element

```java
class Solution {

    public int findKthLargest(int[] nums, int k) {

        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        for (int num : nums) {

            minHeap.offer(num);

            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        return minHeap.peek();
    }
}
```

**Idea:** min-heap of size k — peek is kth largest.  
**T:** O(n log k) · **S:** O(k)

### Top K Frequent (heap)

```java
class Solution {

    public int[] topKFrequent(int[] nums, int k) {

        Map<Integer, Integer> frequency = new HashMap<>();

        for (int num : nums) {
            frequency.put(
                num,
                frequency.getOrDefault(num, 0) + 1
            );
        }


        PriorityQueue<Integer> minHeap =
            new PriorityQueue<>(
                (a, b) -> frequency.get(a) - frequency.get(b)
            );


        for (int num : frequency.keySet()) {

            minHeap.offer(num);

            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }


        int[] result = new int[k];

        int index = 0;

        while (!minHeap.isEmpty()) {
            result[index++] = minHeap.poll();
        }


        return result;
    }
}
```

**Idea:** freq map + min-heap of size k by frequency.  
**T:** O(n log k) · **S:** O(n)  
**Prefer:** bucket sort under Hashing — O(n) when possible.

### K Closest Points (heap)

```java
class Solution {

    public int[][] kClosest(int[][] points, int k) {

        PriorityQueue<int[]> maxHeap =
            new PriorityQueue<>(
                (a, b) -> distance(b) - distance(a)
            );


        for (int[] point : points) {

            maxHeap.offer(point);

            if (maxHeap.size() > k) {
                maxHeap.poll();
            }
        }


        int[][] result = new int[k][2];

        int index = 0;

        while (!maxHeap.isEmpty()) {
            result[index++] = maxHeap.poll();
        }


        return result;
    }


    private int distance(int[] point) {

        int x = point[0];
        int y = point[1];

        return x * x + y * y;
    }
}
```

**Idea:** max-heap of size k by distance²; poll farthest when oversized.  
**T:** O(n log k) · **S:** O(k)

### Merge K Sorted Lists

```java
class Solution {

    public ListNode mergeKLists(ListNode[] lists) {

        if (lists == null || lists.length == 0) {
            return null;
        }


        PriorityQueue<ListNode> minHeap =
            new PriorityQueue<>(
                (a, b) -> a.val - b.val
            );


        // Add first node of every list
        for (ListNode node : lists) {

            if (node != null) {
                minHeap.offer(node);
            }
        }


        ListNode dummy = new ListNode(0);
        ListNode current = dummy;


        while (!minHeap.isEmpty()) {

            ListNode smallest = minHeap.poll();


            current.next = smallest;
            current = current.next;


            // Add next node from the same list
            if (smallest.next != null) {
                minHeap.offer(smallest.next);
            }
        }


        return dummy.next;
    }
}
```

**Idea:** min-heap of k heads; poll smallest, push its next.  
**T:** O(N log k) · **S:** O(k) — `N` = total nodes

### Find Median from Data Stream

```java
class MedianFinder {

    private PriorityQueue<Integer> left;
    private PriorityQueue<Integer> right;

    public MedianFinder() {

        // Max Heap
        left = new PriorityQueue<>((a, b) -> b - a);

        // Min Heap
        right = new PriorityQueue<>();
    }


    public void addNum(int num) {

        left.offer(num);

        right.offer(left.poll());

        if (right.size() > left.size()) {
            left.offer(right.poll());
        }
    }


    public double findMedian() {

        if (left.size() == right.size()) {
            return (left.peek() + right.peek()) / 2.0;
        }

        return left.peek();
    }
}
```

**Idea:** two heaps — max-left / min-right; keep `|sizes|` balanced (left ≥ right).  
**T:** O(log n) add · **S:** O(n)

### Sliding Window Median

```java
class Solution {

    private PriorityQueue<Integer> small;   // Max Heap
    private PriorityQueue<Integer> large;   // Min Heap

    private Map<Integer, Integer> delayed;

    private int smallSize;
    private int largeSize;

    public double[] medianSlidingWindow(int[] nums, int k) {

        small = new PriorityQueue<>((a, b) -> Integer.compare(b, a));
        large = new PriorityQueue<>(Integer::compare);

        delayed = new HashMap<>();

        smallSize = 0;
        largeSize = 0;

        double[] answer = new double[nums.length - k + 1];

        for (int i = 0; i < k; i++) {
            addNum(nums[i]);
        }

        answer[0] = median(k);

        int index = 1;

        for (int i = k; i < nums.length; i++) {

            addNum(nums[i]);
            removeNum(nums[i - k]);

            answer[index++] = median(k);
        }

        return answer;
    }


    private void addNum(int num) {

        if (small.isEmpty() || num <= small.peek()) {

            small.offer(num);
            smallSize++;

        } else {

            large.offer(num);
            largeSize++;
        }

        makeBalance();
    }


    private void removeNum(int num) {

        delayed.put(num, delayed.getOrDefault(num, 0) + 1);

        if (num <= small.peek()) {

            smallSize--;

            if (num == small.peek()) {
                prune(small);
            }

        } else {

            largeSize--;

            if (!large.isEmpty() && num == large.peek()) {
                prune(large);
            }
        }

        makeBalance();
    }


    private void makeBalance() {

        if (smallSize > largeSize + 1) {

            large.offer(small.poll());

            smallSize--;
            largeSize++;

            prune(small);

        } else if (smallSize < largeSize) {

            small.offer(large.poll());

            smallSize++;
            largeSize--;

            prune(large);
        }
    }


    private void prune(PriorityQueue<Integer> heap) {

        while (!heap.isEmpty()) {

            int num = heap.peek();

            Integer count = delayed.get(num);

            if (count == null) {
                break;
            }

            if (count == 1) {
                delayed.remove(num);
            } else {
                delayed.put(num, count - 1);
            }

            heap.poll();
        }
    }


    private double median(int k) {

        if ((k & 1) == 1) {
            return (double) small.peek();
        }

        return ((long) small.peek() + (long) large.peek()) / 2.0;
    }
}
```

**Idea:** two heaps + lazy delete (`delayed`); track logical sizes, prune tops.  
**T:** O(n log n) · **S:** O(n)

### Task Scheduler

```java
class Solution {

    public int leastInterval(char[] tasks, int n) {

        if (n == 0) {
            return tasks.length;
        }

        int[] frequency = new int[26];

        for (char task : tasks) {
            frequency[task - 'A']++;
        }

        PriorityQueue<Integer> maxHeap =
                new PriorityQueue<>(Collections.reverseOrder());

        for (int count : frequency) {
            if (count > 0) {
                maxHeap.offer(count);
            }
        }

        Queue<int[]> cooldown = new ArrayDeque<>();
        int time = 0;

        while (!maxHeap.isEmpty() || !cooldown.isEmpty()) {

            time++;

            if (!maxHeap.isEmpty()) {

                int remaining = maxHeap.poll() - 1;

                if (remaining > 0) {
                    cooldown.offer(new int[]{remaining, time + n});
                }
            }

            if (!cooldown.isEmpty() && cooldown.peek()[1] == time) {
                maxHeap.offer(cooldown.poll()[0]);
            }
        }

        return time;
    }
}
```

**Idea:** max-heap of counts + cooldown queue `(remaining, readyAt)`.  
**T:** O(n log k) · **S:** O(k) — `k` = distinct tasks (≤ 26)

---

## Trees → Trie

### Implement Trie

```java
class Trie {
    boolean isEnd;
    Trie[] children;

    public Trie() {
        isEnd = false;
        children = new Trie[26];
    }
    
    public void insert(String word) {
        Trie curr = this;
        for(char c : word.toCharArray()){
            int idx = c - 'a';
            if(curr.children[idx] == null){
                curr.children[idx] = new Trie();
            }
            curr = curr.children[idx];
        }
        curr.isEnd = true;
    }
    
    public boolean search(String word) {
        Trie curr = this;
        for(char c : word.toCharArray()){
            int idx = c - 'a';
            if(curr.children[idx] == null) return false;
            curr = curr.children[idx];
        }
        return curr.isEnd;
    }
    
    public boolean startsWith(String prefix) {
        Trie curr = this;
        for(char c : prefix.toCharArray()){
            int idx = c - 'a';
            if(curr.children[idx] == null) return false;
            curr = curr.children[idx];
        }
        return true;
    }
}
```

**Idea:** 26-ary tree; `isEnd` marks word; walk path for search/prefix.  
**T:** O(L) per op · **S:** O(Σ) — `L` = word length, `Σ` = total chars stored

### Word Search II

```java
class Solution {

    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        String word;
    }

    private final List<String> result = new ArrayList<>();
    private int rows;
    private int cols;

    public List<String> findWords(char[][] board, String[] words) {

        rows = board.length;
        cols = board[0].length;

        TrieNode root = buildTrie(words);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                dfs(board, row, col, root);
            }
        }

        return result;
    }

    private TrieNode buildTrie(String[] words) {

        TrieNode root = new TrieNode();

        for (String word : words) {
            TrieNode curr = root;

            for (char c : word.toCharArray()) {
                int index = c - 'a';

                if (curr.children[index] == null) {
                    curr.children[index] = new TrieNode();
                }

                curr = curr.children[index];
            }

            curr.word = word;
        }

        return root;
    }

    private void dfs(char[][] board, int row, int col, TrieNode node) {

        if (row < 0 || row >= rows ||
            col < 0 || col >= cols ||
            board[row][col] == '#') {
            return;
        }

        char c = board[row][col];
        TrieNode next = node.children[c - 'a'];

        if (next == null) {
            return;
        }

        // Found a complete word.
        if (next.word != null) {
            result.add(next.word);

            // Prevent adding the same word again.
            next.word = null;
        }

        // Mark current cell as visited.
        board[row][col] = '#';

        dfs(board, row + 1, col, next);
        dfs(board, row - 1, col, next);
        dfs(board, row, col + 1, next);
        dfs(board, row, col - 1, next);

        // Backtrack.
        board[row][col] = c;
    }
}
```

**Idea:** Trie of words + board DFS; null out `word` to dedupe; `#` mark + backtrack.  
**T:** O(mn · 4ᴸ) · **S:** O(words) — `L` = max word length

### Design Add and Search Words

```java
class WordDictionary {

    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isWord;
    }

    private final TrieNode root = new TrieNode();

    public void addWord(String word) {
        TrieNode curr = root;

        for (char c : word.toCharArray()) {
            int index = c - 'a';

            if (curr.children[index] == null) {
                curr.children[index] = new TrieNode();
            }

            curr = curr.children[index];
        }

        curr.isWord = true;
    }

    public boolean search(String word) {
        return search(root, word, 0);
    }

    private boolean search(TrieNode node, String word, int index) {

        if (index == word.length()) {
            return node.isWord;
        }

        char c = word.charAt(index);

        // Normal character
        if (c != '.') {
            TrieNode next = node.children[c - 'a'];

            if (next == null) {
                return false;
            }

            return search(next, word, index + 1);
        }

        // Wildcard: try every possible character
        for (TrieNode next : node.children) {

            if (next != null && search(next, word, index + 1)) {
                return true;
            }
        }

        return false;
    }
}
```

**Idea:** Trie + DFS search; `.` branches over all 26 children.  
**T:** O(26ᴸ) worst (all `.`) · **S:** O(Σ)

### Longest Word in Dictionary

```java
class Solution {

    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isWord;
    }

    private String best = "";

    public String longestWord(String[] words) {

        TrieNode root = new TrieNode();

        // Build Trie
        for (String word : words) {
            insert(root, word);
        }

        // Find longest valid word
        dfs(root, new StringBuilder());

        return best;
    }

    private void insert(TrieNode root, String word) {

        TrieNode curr = root;

        for (char c : word.toCharArray()) {

            int index = c - 'a';

            if (curr.children[index] == null) {
                curr.children[index] = new TrieNode();
            }

            curr = curr.children[index];
        }

        curr.isWord = true;
    }

    private void dfs(TrieNode node, StringBuilder path) {

        for (int i = 0; i < 26; i++) {

            TrieNode child = node.children[i];

            // Every prefix must be a valid word.
            if (child == null || !child.isWord) {
                continue;
            }

            path.append((char) ('a' + i));

            String word = path.toString();

            if (word.length() > best.length() ||
                (word.length() == best.length() && word.compareTo(best) < 0)) {
                best = word;
            }

            dfs(child, path);

            path.deleteCharAt(path.length() - 1);
        }
    }
}
```

**Idea:** Trie DFS; only walk `isWord` children (every prefix builds); keep longest / lex-smallest.  
**T:** O(N·L) · **S:** O(Σ)

### Replace Words

```java
class Solution {

    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isWord;
    }

    public String replaceWords(List<String> dictionary, String sentence) {

        TrieNode root = new TrieNode();

        // Build Trie.
        for (String rootWord : dictionary) {
            insert(root, rootWord);
        }

        StringBuilder result = new StringBuilder();

        for (String word : sentence.split(" ")) {

            if (result.length() > 0) {
                result.append(' ');
            }

            result.append(findRoot(root, word));
        }

        return result.toString();
    }

    private void insert(TrieNode root, String word) {

        TrieNode curr = root;

        for (char c : word.toCharArray()) {

            int index = c - 'a';

            if (curr.children[index] == null) {
                curr.children[index] = new TrieNode();
            }

            curr = curr.children[index];
        }

        curr.isWord = true;
    }

    private String findRoot(TrieNode root, String word) {

        TrieNode curr = root;

        for (int i = 0; i < word.length(); i++) {

            int index = word.charAt(i) - 'a';

            if (curr.children[index] == null) {
                return word;
            }

            curr = curr.children[index];

            // First root found = shortest root.
            if (curr.isWord) {
                return word.substring(0, i + 1);
            }
        }

        return word;
    }
}
```

**Idea:** Trie of roots; walk each word until first `isWord` (shortest root) else keep word.  
**T:** O(N·L) · **S:** O(Σ)

---

## DP → 1D

### Climbing Stairs
**Known** — fib roll (`a,b`).  
**State:** `dp[i]` = ways to climb `i` stairs (using 1 or 2). **T:** O(n) · **S:** O(1)

### House Robber
**Known** — take/skip roll.  
**State:** `dp[i]` = max money robbing houses `0..i`. **T:** O(n) · **S:** O(1)

### House Robber II
**Known** — rob `[0..n-2]` and `[1..n-1]`, take max.  
**State:** same as House Robber on each linear range. **T:** O(n) · **S:** O(1)

### Min Cost Climbing Stairs

```java
class Solution {

    public int minCostClimbingStairs(int[] cost) {

        // State: dp[i] = min cost to reach step i (top = n)
        int prev2 = 0; // dp[i - 2]
        int prev1 = 0; // dp[i - 1]

        for (int i = 2; i <= cost.length; i++) {

            int current = Math.min(
                prev1 + cost[i - 1],
                prev2 + cost[i - 2]
            );

            prev2 = prev1;
            prev1 = current;
        }

        return prev1;
    }
}
```

**Idea:** `dp[i] = min(dp[i-1]+cost[i-1], dp[i-2]+cost[i-2])`; roll 2 vars.  
**State:** `dp[i]` = min cost to reach index `i` (pay when leaving a step).  
**T:** O(n) · **S:** O(1)

### Decode Ways

```java
class Solution {

    public int numDecodings(String s) {

        if (s == null || s.isEmpty() || s.charAt(0) == '0') {
            return 0;
        }

        // State: dp[i] = ways to decode s[0..i-1]
        int prev2 = 1; // ways for empty prefix
        int prev1 = 1; // ways for first char (valid)

        for (int i = 1; i < s.length(); i++) {

            int current = 0;

            int one = s.charAt(i) - '0';
            int two = (s.charAt(i - 1) - '0') * 10 + one;

            if (one >= 1 && one <= 9) {
                current += prev1;
            }

            if (two >= 10 && two <= 26) {
                current += prev2;
            }

            prev2 = prev1;
            prev1 = current;
        }

        return prev1;
    }
}
```

**Idea:** digit DP-lite — 1-digit / 2-digit valid → `dp[i] = dp[i-1] + dp[i-2]`.  
**State:** `dp[i]` = # ways to decode first `i` chars.  
**T:** O(n) · **S:** O(1)

### Maximum Subarray (Kadane)

```java
class Solution {

    public int maxSubArray(int[] nums) {

        // State: running = max sum of subarray ending at i; best = global max
        int best = nums[0];
        int running = nums[0];

        for (int i = 1; i < nums.length; i++) {

            running = Math.max(nums[i], running + nums[i]);
            best = Math.max(best, running);
        }

        return best;
    }
}
```

**Idea:** Kadane — extend or restart at `nums[i]`; track global max.  
**State:** `running` = best sum ending here; `best` = answer so far.  
**T:** O(n) · **S:** O(1)

### Jump Game

```java
class Solution {

    public boolean canJump(int[] nums) {

        // State: reach = farthest index reachable from 0..i so far
        int reach = 0;

        for (int i = 0; i < nums.length; i++) {

            if (i > reach) {
                return false;
            }

            reach = Math.max(reach, i + nums[i]);

            if (reach >= nums.length - 1) {
                return true;
            }
        }

        return true;
    }
}
```

**Idea:** greedy max-reach (DP view: `reach[i]`); fail if index beyond reach.  
**State:** `reach` = max index reachable using jumps from `0..i`.  
**T:** O(n) · **S:** O(1)

---

## DP → Grid

### Unique Paths
**Known** — `dp[j] += dp[j-1]` row roll.  
**State:** `dp[j]` = ways to reach cell `(currRow, j)`. **T:** O(mn) · **S:** O(n)

### Unique Paths II

```java
class Solution {

    public int uniquePathsWithObstacles(int[][] obstacleGrid) {

        int rows = obstacleGrid.length;
        int cols = obstacleGrid[0].length;

        // State: dp[col] = ways to reach (row, col) in current row roll
        int[] dp = new int[cols];
        dp[0] = 1;

        for (int row = 0; row < rows; row++) {

            for (int col = 0; col < cols; col++) {

                if (obstacleGrid[row][col] == 1) {
                    dp[col] = 0;
                } else if (col > 0) {
                    dp[col] += dp[col - 1];
                }
            }
        }

        return dp[cols - 1];
    }
}
```

**Idea:** same 1D roll as Unique Paths; obstacle → `dp[col] = 0`.  
**State:** `dp[col]` = # paths to `(row, col)` (rolling over rows).  
**T:** O(mn) · **S:** O(n)

### Minimum Path Sum

```java
class Solution {

    public int minPathSum(int[][] grid) {

        int rows = grid.length;
        int cols = grid[0].length;

        // State: dp[col] = min path sum to (row, col) in current row roll
        int[] dp = new int[cols];

        dp[0] = grid[0][0];

        // First row
        for (int col = 1; col < cols; col++) {
            dp[col] = dp[col - 1] + grid[0][col];
        }

        for (int row = 1; row < rows; row++) {

            // First column
            dp[0] += grid[row][0];

            for (int col = 1; col < cols; col++) {

                dp[col] = grid[row][col]
                        + Math.min(dp[col], dp[col - 1]);
            }
        }

        return dp[cols - 1];
    }
}
```

**Idea:** 1D roll — `dp[col] = grid + min(from above, from left)`.  
**State:** `dp[col]` = min sum to reach `(row, col)`.  
**T:** O(mn) · **S:** O(n)

---

## DP → Knapsack

### Subset Sum
**Known** — 0/1 knapsack bool DP (backward 1D).  
**State:** `dp[s]` = can we form sum `s`? **T:** O(nS) · **S:** O(S)

### Partition Equal Subset Sum

```java
class Solution {

    public boolean canPartition(int[] nums) {

        int sum = 0;

        for (int num : nums) {
            sum += num;
        }

        // Equal partition is impossible with an odd total.
        if ((sum & 1) == 1) {
            return false;
        }

        int target = sum / 2;

        // State: dp[s] = true if some subset sums to s
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;

        for (int num : nums) {

            // Iterate backwards so each number is used at most once.
            for (int current = target; current >= num; current--) {
                dp[current] = dp[current] || dp[current - num];
            }
        }

        return dp[target];
    }
}
```

**Idea:** 0/1 subset-sum to `sum/2`; odd sum → false; backward 1D DP.  
**State:** `dp[s]` = whether a subset sums to `s` (target = `sum/2`).  
**T:** O(nS) · **S:** O(S) — `S = sum/2`

### Target Sum

```java
class Solution {

    public int findTargetSumWays(int[] nums, int target) {

        int total = 0;

        for (int num : nums) {
            total += num;
        }

        // Impossible if target is outside the achievable range.
        if (Math.abs(target) > total) {
            return 0;
        }

        // (total + target) must be even.
        if ((total + target) % 2 != 0) {
            return 0;
        }

        int subsetSum = (total + target) / 2;

        // State: dp[s] = # ways to pick a subset summing to s
        int[] dp = new int[subsetSum + 1];
        dp[0] = 1;

        for (int num : nums) {

            // Go backwards because every number can be used only once.
            for (int sum = subsetSum; sum >= num; sum--) {
                dp[sum] += dp[sum - num];
            }
        }

        return dp[subsetSum];
    }
}
```

**Idea:** ± assign → subset with sum `(total+target)/2`; count ways (0/1).  
**State:** `dp[s]` = ways to form sum `s` (positive group).  
**T:** O(nS) · **S:** O(S)

### Last Stone Weight II

```java
class Solution {

    public int lastStoneWeightII(int[] stones) {

        int total = 0;

        for (int stone : stones) {
            total += stone;
        }

        int target = total / 2;

        // State: dp[s] = true if subset of stones sums to s
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;

        for (int stone : stones) {

            // Backwards so each stone is used only once.
            for (int sum = target; sum >= stone; sum--) {
                dp[sum] = dp[sum] || dp[sum - stone];
            }
        }

        // Find the largest achievable sum <= total / 2.
        for (int sum = target; sum >= 0; sum--) {
            if (dp[sum]) {
                return total - 2 * sum;
            }
        }

        return 0;
    }
}
```

**Idea:** partition → max subset ≤ `total/2`; leftover = `total - 2·sum`.  
**State:** `dp[s]` = can form pile sum `s`; answer = `total - 2·bestS`.  
**T:** O(nS) · **S:** O(S)

### Coin Change II

```java
class Solution {

    public int change(int amount, int[] coins) {

        // State: dp[a] = # combinations that sum to amount a
        int[] dp = new int[amount + 1];

        // One way to make amount 0: choose nothing.
        dp[0] = 1;

        for (int coin : coins) {

            for (int current = coin; current <= amount; current++) {

                dp[current] += dp[current - coin];
            }
        }

        return dp[amount];
    }
}
```

**Idea:** unbounded ways; outer coins (order), forward 1D.  
**State:** `dp[a]` = number of combinations to make amount `a`.  
**T:** O(n·amount) · **S:** O(amount)

### Integer Break

```java
class Solution {

    public int integerBreak(int n) {

        // State: dp[x] = max product breaking integer x into ≥2 parts
        int[] dp = new int[n + 1];

        dp[1] = 1;

        for (int num = 2; num <= n; num++) {

            for (int first = 1; first < num; first++) {

                int second = num - first;

                dp[num] = Math.max(
                    dp[num],
                    first * Math.max(second, dp[second])
                );
            }
        }

        return dp[n];
    }
}
```

**Idea:** split `num = first + second`; max product using `second` raw or broken.  
**State:** `dp[x]` = max product from breaking `x` (must break at least once for answer).  
**T:** O(n²) · **S:** O(n)

---

## DP → String

### Longest Common Subsequence

```java
class Solution {

    public int longestCommonSubsequence(String text1, String text2) {

        int m = text1.length();
        int n = text2.length();

        // State: dp[j] = LCS of text1[0..i-1] and text2[0..j-1] (rolling i)
        int[] dp = new int[n + 1];

        for (int i = 1; i <= m; i++) {

            int diagonal = 0;

            for (int j = 1; j <= n; j++) {

                int above = dp[j];

                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[j] = diagonal + 1;
                } else {
                    dp[j] = Math.max(dp[j], dp[j - 1]);
                }

                diagonal = above;
            }
        }

        return dp[n];
    }
}
```

**Idea:** match → diag+1; else max(up, left); 1D roll + `diagonal`.  
**State:** `dp[j]` = LCS length using `text1` prefix `i` and `text2` prefix `j`.  
**T:** O(mn) · **S:** O(n)

### Edit Distance

```java
class Solution {

    public int minDistance(String word1, String word2) {

        int m = word1.length();
        int n = word2.length();

        // State: dp[j] = min edits to convert word1[0..i-1] → word2[0..j-1]
        int[] dp = new int[n + 1];

        // Convert empty word1 to first j characters of word2.
        for (int j = 0; j <= n; j++) {
            dp[j] = j;
        }

        for (int i = 1; i <= m; i++) {

            int diagonal = dp[0];

            // Convert first i characters of word1 to empty word2.
            dp[0] = i;

            for (int j = 1; j <= n; j++) {

                int above = dp[j];

                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {

                    dp[j] = diagonal;

                } else {

                    dp[j] = 1 + Math.min(
                        diagonal,                 // replace
                        Math.min(dp[j], dp[j - 1]) // delete, insert
                    );
                }

                diagonal = above;
            }
        }

        return dp[n];
    }
}
```

**Idea:** match → diag; else 1 + min(replace, delete, insert); 1D roll.  
**State:** `dp[j]` = min ops for `word1` prefix `i` → `word2` prefix `j`.  
**T:** O(mn) · **S:** O(n)

### Longest Palindromic Subsequence

```java
class Solution {

    public int longestPalindromeSubseq(String s) {

        int n = s.length();

        // State: LCS(s, reverse(s)) — dp[j] = LPS via LCS roll on s vs reverse(s)
        int[] dp = new int[n + 1];

        for (int i = 1; i <= n; i++) {

            int diagonal = 0;

            for (int j = 1; j <= n; j++) {

                int above = dp[j];

                if (s.charAt(i - 1) == s.charAt(n - j)) {
                    dp[j] = diagonal + 1;
                } else {
                    dp[j] = Math.max(dp[j], dp[j - 1]);
                }

                diagonal = above;
            }
        }

        return dp[n];
    }
}
```

**Idea:** LPS = LCS(`s`, `reverse(s)`); same 1D LCS roll.  
**State:** `dp[j]` = LCS of `s[0..i-1]` and `reverse(s)[0..j-1]`.  
**T:** O(n²) · **S:** O(n)

### Word Break

```java
class Solution {

    public boolean wordBreak(String s, List<String> wordDict) {

        Set<String> words = new HashSet<>(wordDict);

        // State: dp[i] = true if s[0..i-1] can be segmented into dict words
        boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true;

        for (int i = 1; i <= s.length(); i++) {

            for (int j = 0; j < i; j++) {

                if (dp[j] && words.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }

        return dp[s.length()];
    }
}
```

**Idea:** `dp[i]` if some `j < i` with `dp[j]` and `s[j..i)` in dict.  
**State:** `dp[i]` = can segment prefix of length `i`.  
**T:** O(n²·k) · **S:** O(n) — `k` ≈ avg substring / dict lookup cost

---

## DP → Subsequence

### LIS (DP)

```java
class Solution {

    public int lengthOfLIS(int[] nums) {

        int n = nums.length;

        // State: dp[i] = length of LIS ending at index i
        int[] dp = new int[n];

        Arrays.fill(dp, 1);

        int answer = 1;

        for (int i = 1; i < n; i++) {

            for (int j = 0; j < i; j++) {

                if (nums[j] < nums[i]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }

            answer = Math.max(answer, dp[i]);
        }

        return answer;
    }
}
```

**Idea:** for each `i`, extend best `j < i` with `nums[j] < nums[i]`.  
**State:** `dp[i]` = LIS length ending at `i`; answer = max `dp[i]`.  
**T:** O(n²) · **S:** O(n)

### LIS (patience / BS)

```java
class Solution {

    public int lengthOfLIS(int[] nums) {

        // State: tails[len-1] = smallest tail of all increasing subseqs of length len
        int[] tails = new int[nums.length];
        int size = 0;

        for (int num : nums) {

            int left = 0;
            int right = size;

            while (left < right) {
                int mid = left + (right - left) / 2;

                if (tails[mid] < num) {
                    left = mid + 1;
                } else {
                    right = mid;
                }
            }

            tails[left] = num;

            if (left == size) {
                size++;
            }
        }

        return size;
    }
}
```

**Idea:** patience sorting — BS replace first `tails[i] >= num`; else append.  
**State:** `tails[i]` = smallest ending value of an IS of length `i+1`; `size` = LIS len.  
**T:** O(n log n) · **S:** O(n)

### Russian Doll Envelopes

```java
class Solution {

    public int maxEnvelopes(int[][] envelopes) {

        Arrays.sort(envelopes, (a, b) -> {
            if (a[0] != b[0]) {
                return Integer.compare(a[0], b[0]);
            }
            return Integer.compare(b[1], a[1]);
        });

        // State: tails[len-1] = smallest height ending an increasing chain of length len
        int[] tails = new int[envelopes.length];
        int size = 0;

        for (int[] envelope : envelopes) {

            int height = envelope[1];

            int left = 0;
            int right = size;

            // Find first height >= current height.
            while (left < right) {

                int mid = left + (right - left) / 2;

                if (tails[mid] < height) {
                    left = mid + 1;
                } else {
                    right = mid;
                }
            }

            tails[left] = height;

            if (left == size) {
                size++;
            }
        }

        return size;
    }
}
```

**Idea:** sort w↑ h↓ (same w can't nest); LIS on heights (patience).  
**State:** `tails[i]` = smallest height of envelope chain length `i+1`.  
**T:** O(n log n) · **S:** O(n)

---

## DP → Tree

### House Robber III

```java
class Solution {

    public int rob(TreeNode root) {
        int[] result = dfs(root);
        return Math.max(result[0], result[1]);
    }

    // result[0] = maximum if we rob this node
    // result[1] = maximum if we skip this node
    private int[] dfs(TreeNode node) {

        if (node == null) {
            return new int[] {0, 0};
        }

        int[] left = dfs(node.left);
        int[] right = dfs(node.right);

        int rob = node.val + left[1] + right[1];

        int skip = Math.max(left[0], left[1])
                 + Math.max(right[0], right[1]);

        return new int[] {rob, skip};
    }
}
```

**Idea:** tree DP pair `(rob, skip)`; rob → must skip kids; skip → max of each kid.  
**State:** `dfs(node) = [rob, skip]` max money for subtree.  
**T:** O(n) · **S:** O(h)

### Binary Tree Max Path Sum

```java
class Solution {

    private int maxSum = Integer.MIN_VALUE;

    public int maxPathSum(TreeNode root) {

        maxGain(root);

        return maxSum;
    }


    private int maxGain(TreeNode node) {

        if (node == null) {
            return 0;
        }


        int leftGain = Math.max(0, maxGain(node.left));
        int rightGain = Math.max(0, maxGain(node.right));


        // Best path passing through this node
        maxSum = Math.max(
            maxSum,
            node.val + leftGain + rightGain
        );


        // Return best single branch
        return node.val + Math.max(leftGain, rightGain);
    }
}
```

**Idea:** tree DP — clamp child gains at 0; update global with L+R+node.  
**State:** `maxGain(node)` = best downward path starting at `node`; `maxSum` = best any path.  
**T:** O(n) · **S:** O(h)

### Diameter of Binary Tree

```java
class Solution {

    private int diameter = 0;

    public int diameterOfBinaryTree(TreeNode root) {

        height(root);

        return diameter;
    }


    private int height(TreeNode node) {

        if (node == null) {
            return 0;
        }


        int leftHeight = height(node.left);
        int rightHeight = height(node.right);


        // Longest path passing through this node
        diameter = Math.max(
            diameter,
            leftHeight + rightHeight
        );


        // Return height to parent
        return 1 + Math.max(leftHeight, rightHeight);
    }
}
```

**Idea:** tree DP — at each node update diameter = L+R heights.  
**State:** `height(node)` = subtree height; `diameter` = max edges on any path.  
**T:** O(n) · **S:** O(h)

---

## Greedy → Interval

### Non-overlapping Intervals
**Known / done above** — see Arrays → Sorting: Non-overlapping Intervals.  
**T:** O(n log n) · **S:** O(1)

### Meeting Rooms II
**Known / done above** — see Arrays → Sorting: Meeting Rooms II.  
**T:** O(n log n) · **S:** O(n)

### Minimum Platforms
**Known** — sort arrivals/departures + 2ptr (same idea as Meeting Rooms II sweep).  
**T:** O(n log n) · **S:** O(1)

### Insert / Merge Intervals
**Known / done above** — see Arrays → Sorting: Insert Interval, Merge Intervals.  
**T:** O(n) / O(n log n) · **S:** O(n)

---

## Greedy → Array

### Jump Game I
**Known / done above** — see DP → 1D: Jump Game (max reach).  
**T:** O(n) · **S:** O(1)

### Jump Game II
**Known** — window jumps (greedy BFS levels).  
**T:** O(n) · **S:** O(1)

### Gas Station

```java
class Solution {

    public int canCompleteCircuit(int[] gas, int[] cost) {

        int totalGas = 0;
        int tank = 0;
        int start = 0;

        for (int i = 0; i < gas.length; i++) {

            int gain = gas[i] - cost[i];

            totalGas += gain;
            tank += gain;

            // Current start cannot reach station i + 1.
            if (tank < 0) {
                start = i + 1;
                tank = 0;
            }
        }

        return totalGas >= 0 ? start : -1;
    }
}
```

**Idea:** net tank; if tank drops < 0, restart at `i+1`; feasible iff `totalGas ≥ 0`.  
**T:** O(n) · **S:** O(1)

### Candy

```java
class Solution {

    public int candy(int[] ratings) {

        int n = ratings.length;
        int[] candies = new int[n];

        Arrays.fill(candies, 1);

        // Left to right:
        // Higher rating than left neighbor -> more candy.
        for (int i = 1; i < n; i++) {
            if (ratings[i] > ratings[i - 1]) {
                candies[i] = candies[i - 1] + 1;
            }
        }

        // Right to left:
        // Higher rating than right neighbor -> more candy.
        for (int i = n - 2; i >= 0; i--) {
            if (ratings[i] > ratings[i + 1]) {
                candies[i] = Math.max(candies[i], candies[i + 1] + 1);
            }
        }

        int total = 0;

        for (int candy : candies) {
            total += candy;
        }

        return total;
    }
}
```

**Idea:** two-pass slope — L→R then R→L; take max to satisfy both neighbors.  
**T:** O(n) · **S:** O(n)

### Assign Cookies

```java
class Solution {

    public int findContentChildren(int[] g, int[] s) {

        Arrays.sort(g);
        Arrays.sort(s);

        int child = 0;
        int cookie = 0;

        while (child < g.length && cookie < s.length) {

            if (s[cookie] >= g[child]) {
                child++;
            }

            cookie++;
        }

        return child;
    }
}
```

**Idea:** sort both; greedy give smallest cookie that fits next child.  
**T:** O(n log n) · **S:** O(1)

### Boats to Save People

```java
class Solution {

    public int numRescueBoats(int[] people, int limit) {

        Arrays.sort(people);

        int left = 0;
        int right = people.length - 1;
        int boats = 0;

        while (left <= right) {

            if (people[left] + people[right] <= limit) {
                left++;
            }

            right--;
            boats++;
        }

        return boats;
    }
}
```

**Idea:** sort + 2ptr — heaviest always boards; lightest joins if fits.  
**T:** O(n log n) · **S:** O(1)

---

## Greedy → Freq / Scheduling

### Majority Element

```java
class Solution {

    public int majorityElement(int[] nums) {

        int candidate = 0;
        int count = 0;

        for (int num : nums) {

            if (count == 0) {
                candidate = num;
            }

            count += (num == candidate) ? 1 : -1;
        }

        return candidate;
    }
}
```

**Idea:** Boyer-Moore voting — cancel pairs; remaining candidate is majority.  
**T:** O(n) · **S:** O(1)

### Task Scheduler
**Known / done above** — see Trees → Heap: Task Scheduler.  
**T:** O(n log k) / O(n) · **S:** O(k)

### Reorganize String
**Known / done above** — see Arrays → Greedy on Strings: Reorganize String.  
**T:** O(n log 26) · **S:** O(26)

---

## Linked List → Fast & Slow

### Linked List Cycle
**Known** — Floyd slow/fast meet. **T:** O(n) · **S:** O(1)

### Linked List Cycle II

```java
public class Solution {

    public ListNode detectCycle(ListNode head) {

        ListNode slow = head;
        ListNode fast = head;

        // Phase 1: Detect cycle.
        while (fast != null && fast.next != null) {

            slow = slow.next;
            fast = fast.next.next;

            if (slow == fast) {
                break;
            }
        }

        // No cycle.
        if (fast == null || fast.next == null) {
            return null;
        }

        // Phase 2: Find cycle entry.
        slow = head;

        while (slow != fast) {
            slow = slow.next;
            fast = fast.next;
        }

        return slow;
    }
}
```

**Idea:** Floyd — meet in cycle; reset one to head, same speed → entry.  
**T:** O(n) · **S:** O(1)

### Middle of Linked List
**Known** — slow/fast; slow at mid when fast ends. **T:** O(n) · **S:** O(1)

### Happy Number

```java
class Solution {

    public boolean isHappy(int n) {

        Set<Integer> seen = new HashSet<>();

        while (n != 1 && !seen.contains(n)) {

            seen.add(n);
            n = digitSquareSum(n);
        }

        return n == 1;
    }

    private int digitSquareSum(int n) {

        int sum = 0;

        while (n > 0) {
            int digit = n % 10;
            sum += digit * digit;
            n /= 10;
        }

        return sum;
    }
}
```

**Idea:** follow digit-square chain; cycle → unhappy (set); `1` → happy.  
**T:** O(log n) · **S:** O(log n) — Floyd also works O(1) space

### Remove Nth From End

```java
class Solution {

    public ListNode removeNthFromEnd(ListNode head, int n) {

        ListNode dummy = new ListNode(0);
        dummy.next = head;

        ListNode fast = dummy;
        ListNode slow = dummy;

        // Create a gap of n nodes.
        for (int i = 0; i < n; i++) {
            fast = fast.next;
        }

        // Move both until fast reaches the last node.
        while (fast.next != null) {
            fast = fast.next;
            slow = slow.next;
        }

        // slow.next is the node to remove.
        slow.next = slow.next.next;

        return dummy.next;
    }
}
```

**Idea:** dummy + gap of `n`; when fast at end, slow before target.  
**T:** O(n) · **S:** O(1)

---

## Linked List → Reversal

### Reverse Linked List
**Known** — 3ptr (`prev`, `curr`, `next`). **T:** O(n) · **S:** O(1)

### Reverse Linked List II

```java
class Solution {

    public ListNode reverseBetween(ListNode head, int left, int right) {

        if (head == null || left == right) {
            return head;
        }

        ListNode dummy = new ListNode(0);
        dummy.next = head;

        // Move prev to the node just before `left`.
        ListNode prev = dummy;

        for (int i = 1; i < left; i++) {
            prev = prev.next;
        }

        // `curr` is the first node that needs to be reversed.
        ListNode curr = prev.next;

        // Reverse the sublist using front insertion.
        for (int i = 0; i < right - left; i++) {

            ListNode next = curr.next;

            curr.next = next.next;
            next.next = prev.next;
            prev.next = next;
        }

        return dummy.next;
    }
}
```

**Idea:** dummy; park `prev` before `left`; front-insert nodes into segment.  
**T:** O(n) · **S:** O(1)

### Reverse Nodes in K-Group

```java
class Solution {

    public ListNode reverseKGroup(ListNode head, int k) {

        ListNode dummy = new ListNode(0);
        dummy.next = head;

        ListNode groupPrev = dummy;

        while (true) {

            // Find the kth node of the current group.
            ListNode kth = getKth(groupPrev, k);

            // Not enough nodes left.
            if (kth == null) {
                break;
            }

            ListNode groupNext = kth.next;

            // Reverse the current group.
            ListNode prev = groupNext;
            ListNode curr = groupPrev.next;

            while (curr != groupNext) {
                ListNode next = curr.next;
                curr.next = prev;
                prev = curr;
                curr = next;
            }

            // Connect previous part to reversed group.
            ListNode oldGroupStart = groupPrev.next;
            groupPrev.next = kth;

            // Move groupPrev to the end of the reversed group.
            groupPrev = oldGroupStart;
        }

        return dummy.next;
    }

    private ListNode getKth(ListNode curr, int k) {

        while (curr != null && k > 0) {
            curr = curr.next;
            k--;
        }

        return curr;
    }
}
```

**Idea:** reverse each full block of `k`; leftover untouched; rewire with `groupPrev`.  
**T:** O(n) · **S:** O(1)

---

## Linked List → Merge

### Merge Two Sorted Lists

```java
class Solution {

    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {

        ListNode dummy = new ListNode(0);
        ListNode curr = dummy;

        while (l1 != null && l2 != null) {

            if (l1.val < l2.val) {
                curr.next = l1;
                l1 = l1.next;
            } else {
                curr.next = l2;
                l2 = l2.next;
            }

            curr = curr.next;
        }

        // Attach remaining nodes.
        if (l1 != null) {
            curr.next = l1;
        } else {
            curr.next = l2;
        }

        return dummy.next;
    }
}
```

**Idea:** dummy + pick smaller head each step; attach leftover.  
**T:** O(n+m) · **S:** O(1)

### Merge K Sorted Lists
**Known / done above** — see Trees → Heap: Merge K Sorted Lists.  
**T:** O(N log k) · **S:** O(k)

### Sort List

```java
class Solution {

    public ListNode sortList(ListNode head) {

        if (head == null || head.next == null) {
            return head;
        }

        // Find length.
        int n = 0;
        ListNode curr = head;

        while (curr != null) {
            n++;
            curr = curr.next;
        }

        ListNode dummy = new ListNode(0);
        dummy.next = head;

        // Merge sublists of size 1, 2, 4, 8, ...
        for (int size = 1; size < n; size *= 2) {

            ListNode prev = dummy;
            curr = dummy.next;

            while (curr != null) {

                // First half.
                ListNode left = curr;

                // Second half.
                ListNode right = split(left, size);

                // Next pair of groups.
                curr = split(right, size);

                // Merge left and right.
                ListNode[] merged = merge(left, right);

                prev.next = merged[0];
                prev = merged[1];
            }
        }

        return dummy.next;
    }

    // Cut the first `size` nodes and return the remaining list.
    private ListNode split(ListNode head, int size) {

        if (head == null) {
            return null;
        }

        for (int i = 1; i < size && head.next != null; i++) {
            head = head.next;
        }

        ListNode next = head.next;
        head.next = null;

        return next;
    }

    // Merge two sorted lists.
    // Returns {head, tail} of merged list.
    private ListNode[] merge(ListNode l1, ListNode l2) {

        ListNode dummy = new ListNode(0);
        ListNode curr = dummy;

        while (l1 != null && l2 != null) {

            if (l1.val <= l2.val) {
                curr.next = l1;
                l1 = l1.next;
            } else {
                curr.next = l2;
                l2 = l2.next;
            }

            curr = curr.next;
        }

        if (l1 != null) {
            curr.next = l1;
        } else {
            curr.next = l2;
        }

        while (curr.next != null) {
            curr = curr.next;
        }

        return new ListNode[] {dummy.next, curr};
    }
}
```

**Idea:** bottom-up merge sort — split by size `1,2,4…`, merge pairs.  
**T:** O(n log n) · **S:** O(1)

---

## Linked List → Structure / Copy

### Reorder List

```java
class Solution {

    public void reorderList(ListNode head) {

        if (head == null || head.next == null) {
            return;
        }

        // 1. Find the middle.
        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        // 2. Reverse the second half.
        ListNode second = slow.next;
        slow.next = null;

        ListNode prev = null;

        while (second != null) {
            ListNode next = second.next;
            second.next = prev;
            prev = second;
            second = next;
        }

        second = prev;

        // 3. Merge the two halves alternately.
        ListNode first = head;

        while (second != null) {

            ListNode firstNext = first.next;
            ListNode secondNext = second.next;

            first.next = second;
            second.next = firstNext;

            first = firstNext;
            second = secondNext;
        }
    }
}
```

**Idea:** mid + reverse 2nd half + alternate merge.  
**T:** O(n) · **S:** O(1)

### Rotate List

```java
class Solution {

    public ListNode rotateRight(ListNode head, int k) {

        if (head == null || head.next == null || k == 0) {
            return head;
        }

        // Find length and tail.
        int n = 1;
        ListNode tail = head;

        while (tail.next != null) {
            tail = tail.next;
            n++;
        }

        // Rotating n times gives the original list.
        k %= n;

        if (k == 0) {
            return head;
        }

        // Make the list circular.
        tail.next = head;

        // Find the new tail.
        int steps = n - k;

        ListNode newTail = head;

        for (int i = 1; i < steps; i++) {
            newTail = newTail.next;
        }

        // New head is after the new tail.
        ListNode newHead = newTail.next;

        // Break the circle.
        newTail.next = null;

        return newHead;
    }
}
```

**Idea:** length + join circle; new tail at `n-k`; break.  
**T:** O(n) · **S:** O(1)

### Odd Even Linked List

```java
class Solution {

    public ListNode oddEvenList(ListNode head) {

        if (head == null || head.next == null) {
            return head;
        }

        ListNode odd = head;
        ListNode even = head.next;
        ListNode evenHead = even;

        while (even != null && even.next != null) {

            // Connect odd nodes.
            odd.next = even.next;
            odd = odd.next;

            // Connect even nodes.
            even.next = odd.next;
            even = even.next;
        }

        // Attach even list after odd list.
        odd.next = evenHead;

        return head;
    }
}
```

**Idea:** relink odds then evens in place; stitch `evenHead` after odd tail.  
**T:** O(n) · **S:** O(1)

### Copy List with Random Pointer

```java
class Solution {
    public Node copyRandomList(Node head) {
        if (head == null) {
            return null;
        }

        HashMap<Node, Node> map = new HashMap<>();

        // Create a copy of every node
        Node current = head;

        while (current != null) {
            map.put(current, new Node(current.val));
            current = current.next;
        }

        // Connect next and random pointers
        current = head;

        while (current != null) {
            Node copy = map.get(current);

            copy.next = map.get(current.next);
            copy.random = map.get(current.random);

            current = current.next;
        }

        return map.get(head);
    }
}
```

**Idea:** map old→new; second pass wire `next` / `random`.  
**T:** O(n) · **S:** O(n)

### Intersection of Two Lists

```java
public class Solution {

    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {

        ListNode a = headA;
        ListNode b = headB;

        while (a != b) {

            a = (a == null) ? headB : a.next;
            b = (b == null) ? headA : b.next;
        }

        return a;
    }
}
```

**Idea:** switch heads when null — equalizes path length; meet at intersection (or both null).  
**T:** O(n+m) · **S:** O(1)

### Add Two Numbers

```java
class Solution {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        int carry = 0;

        while (l1 != null || l2 != null || carry != 0) {
            int sum = carry;

            if (l1 != null) {
                sum += l1.val;
                l1 = l1.next;
            }

            if (l2 != null) {
                sum += l2.val;
                l2 = l2.next;
            }

            carry = sum / 10;

            current.next = new ListNode(sum % 10);
            current = current.next;
        }

        return dummy.next;
    }
}
```

**Idea:** digit-by-digit sum + carry; dummy list; continue while carry.  
**T:** O(max(n,m)) · **S:** O(1) (+ output)

---

## Backtracking → Subsets / Combos / Perms

### Subsets
**Known** — include/exclude DFS. **T:** O(n·2ⁿ) · **S:** O(n)

### Subsets II

```java
class Solution {
    public List<List<Integer>> subsetsWithDup(int[] nums) {
        Arrays.sort(nums);

        List<List<Integer>> result = new ArrayList<>();
        List<Integer> subset = new ArrayList<>();

        dfs(0, nums, subset, result);

        return result;
    }

    private void dfs(
            int index,
            int[] nums,
            List<Integer> subset,
            List<List<Integer>> result) {

        if (index == nums.length) {
            result.add(new ArrayList<>(subset));
            return;
        }

        // Pick
        subset.add(nums[index]);
        dfs(index + 1, nums, subset, result);
        subset.remove(subset.size() - 1);

        // Don't pick: skip duplicates
        while (index + 1 < nums.length &&
               nums[index] == nums[index + 1]) {
            index++;
        }

        dfs(index + 1, nums, subset, result);
    }
}
```

**Idea:** sort; include/exclude; on exclude, skip equal neighbors.  
**T:** O(n·2ⁿ) · **S:** O(n)

### Combinations

```java
class Solution {
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> combination = new ArrayList<>();

        dfs(1, n, k, combination, result);

        return result;
    }

    private void dfs(
            int index,
            int n,
            int k,
            List<Integer> combination,
            List<List<Integer>> result) {

        // Picked exactly k numbers
        if (combination.size() == k) {
            result.add(new ArrayList<>(combination));
            return;
        }

        // Not enough numbers left to complete the combination
        if (index > n || n - index + 1 < k - combination.size()) {
            return;
        }

        // Pick
        combination.add(index);
        dfs(index + 1, n, k, combination, result);
        combination.remove(combination.size() - 1);

        // Don't pick
        dfs(index + 1, n, k, combination, result);
    }
}
```

**Idea:** include/exclude on `1..n`; prune when leftover can't fill `k`.  
**T:** O(C(n,k)·k) · **S:** O(k)

### Combination Sum
**Known** — unbounded pick (`i` reuse); prune `> remaining`. **T:** O(2ᵗ) · **S:** O(t)

### Combination Sum II

```java
class Solution {
    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        Arrays.sort(candidates);

        List<List<Integer>> result = new ArrayList<>();
        List<Integer> combination = new ArrayList<>();

        backtrack(0, target, candidates, combination, result);

        return result;
    }

    private void backtrack(
            int start,
            int remaining,
            int[] candidates,
            List<Integer> combination,
            List<List<Integer>> result) {

        if (remaining == 0) {
            result.add(new ArrayList<>(combination));
            return;
        }

        for (int i = start; i < candidates.length; i++) {

            // Skip duplicates at the same recursion level
            if (i > start && candidates[i] == candidates[i - 1]) {
                continue;
            }

            // Since array is sorted, nothing after this can work
            if (candidates[i] > remaining) {
                break;
            }

            // Pick
            combination.add(candidates[i]);

            // i + 1 because each element can be used only once
            backtrack(i + 1, remaining - candidates[i],
                      candidates, combination, result);

            // Backtrack
            combination.remove(combination.size() - 1);
        }
    }
}
```

**Idea:** sort; for-loop pick once (`i+1`); skip same-level dups; prune `> remaining`.  
**T:** O(2ⁿ) · **S:** O(t)

### Permutations
**Known** — swap or `used[]` DFS. **T:** O(n·n!) · **S:** O(n)

### Permutations II

```java
class Solution {
    public List<List<Integer>> permuteUnique(int[] nums) {
        Arrays.sort(nums);

        List<List<Integer>> result = new ArrayList<>();
        List<Integer> permutation = new ArrayList<>();
        boolean[] used = new boolean[nums.length];

        backtrack(nums, used, permutation, result);

        return result;
    }

    private void backtrack(
            int[] nums,
            boolean[] used,
            List<Integer> permutation,
            List<List<Integer>> result) {

        if (permutation.size() == nums.length) {
            result.add(new ArrayList<>(permutation));
            return;
        }

        for (int i = 0; i < nums.length; i++) {

            // Already used
            if (used[i]) {
                continue;
            }

            // Skip duplicates at the same recursion level
            if (i > 0 && nums[i] == nums[i - 1] && !used[i - 1]) {
                continue;
            }

            // Pick
            used[i] = true;
            permutation.add(nums[i]);

            backtrack(nums, used, permutation, result);

            // Backtrack
            permutation.remove(permutation.size() - 1);
            used[i] = false;
        }
    }
}
```

**Idea:** sort + `used`; skip dup if prev equal and unused (same level).  
**T:** O(n·n!) · **S:** O(n)

---

## Backtracking → Partition / Grid

### Palindrome Partitioning

```java
class Solution {
    public List<List<String>> partition(String s) {
        List<List<String>> result = new ArrayList<>();
        List<String> partition = new ArrayList<>();

        backtrack(0, s, partition, result);

        return result;
    }

    private void backtrack(
            int start,
            String s,
            List<String> partition,
            List<List<String>> result) {

        if (start == s.length()) {
            result.add(new ArrayList<>(partition));
            return;
        }

        for (int end = start; end < s.length(); end++) {

            if (!isPalindrome(s, start, end)) {
                continue;
            }

            // Pick
            partition.add(s.substring(start, end + 1));

            backtrack(end + 1, s, partition, result);

            // Backtrack
            partition.remove(partition.size() - 1);
        }
    }

    private boolean isPalindrome(String s, int left, int right) {
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }

        return true;
    }
}
```

**Idea:** cut at each `end`; only extend if `s[start..end]` is palindrome.  
**T:** O(n·2ⁿ) · **S:** O(n)

### Restore IP Addresses

```java
class Solution {
    public List<String> restoreIpAddresses(String s) {
        List<String> result = new ArrayList<>();
        List<String> parts = new ArrayList<>();

        backtrack(0, s, parts, result);

        return result;
    }

    private void backtrack(
            int start,
            String s,
            List<String> parts,
            List<String> result) {

        // Four parts created
        if (parts.size() == 4) {
            if (start == s.length()) {
                result.add(String.join(".", parts));
            }
            return;
        }

        // Try taking 1, 2, or 3 digits
        for (int end = start; end < Math.min(start + 3, s.length()); end++) {

            String part = s.substring(start, end + 1);

            if (!isValid(part)) {
                continue;
            }

            // Pick
            parts.add(part);

            backtrack(end + 1, s, parts, result);

            // Backtrack
            parts.remove(parts.size() - 1);
        }
    }

    private boolean isValid(String part) {
        // No leading zero unless the number is exactly "0"
        if (part.length() > 1 && part.charAt(0) == '0') {
            return false;
        }

        // Value must be <= 255
        return Integer.parseInt(part) <= 255;
    }
}
```

**Idea:** exactly 4 parts; try 1–3 digits; validate leading zero / ≤255.  
**T:** O(1) · **S:** O(1) — bounded by length ≤12

### Word Search

```java
class Solution {
    public boolean exist(char[][] board, String word) {
        int rows = board.length;
        int cols = board[0].length;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {

                if (dfs(board, word, row, col, 0)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean dfs(
            char[][] board,
            String word,
            int row,
            int col,
            int index) {

        // Matched the entire word
        if (index == word.length()) {
            return true;
        }

        // Out of bounds
        if (row < 0 || row >= board.length ||
            col < 0 || col >= board[0].length) {
            return false;
        }

        // Wrong character or already visited
        if (board[row][col] != word.charAt(index)) {
            return false;
        }

        // Mark as visited
        char original = board[row][col];
        board[row][col] = '#';

        // Explore 4 directions
        boolean found =
                dfs(board, word, row + 1, col, index + 1) ||
                dfs(board, word, row - 1, col, index + 1) ||
                dfs(board, word, row, col + 1, index + 1) ||
                dfs(board, word, row, col - 1, index + 1);

        // Backtrack
        board[row][col] = original;

        return found;
    }
}
```

**Idea:** board DFS match `word[index]`; mark `#` + backtrack.  
**T:** O(mn·4ᴸ) · **S:** O(L)

---

## Backtracking → Constraint

### N-Queens

```java
class Solution {
    public List<List<String>> solveNQueens(int n) {
        List<List<String>> result = new ArrayList<>();

        char[][] board = new char[n][n];

        for (char[] row : board) {
            Arrays.fill(row, '.');
        }

        boolean[] cols = new boolean[n];
        boolean[] diagonals = new boolean[2 * n - 1];
        boolean[] antiDiagonals = new boolean[2 * n - 1];

        backtrack(0, board, cols, diagonals, antiDiagonals, result);

        return result;
    }

    private void backtrack(
            int row,
            char[][] board,
            boolean[] cols,
            boolean[] diagonals,
            boolean[] antiDiagonals,
            List<List<String>> result) {

        // Placed all n queens
        if (row == board.length) {
            List<String> solution = new ArrayList<>();

            for (char[] boardRow : board) {
                solution.add(new String(boardRow));
            }

            result.add(solution);
            return;
        }

        for (int col = 0; col < board.length; col++) {

            int diagonal = row - col + board.length - 1;
            int antiDiagonal = row + col;

            // Can't place queen here
            if (cols[col] ||
                diagonals[diagonal] ||
                antiDiagonals[antiDiagonal]) {
                continue;
            }

            // Pick
            board[row][col] = 'Q';
            cols[col] = true;
            diagonals[diagonal] = true;
            antiDiagonals[antiDiagonal] = true;

            backtrack(
                    row + 1,
                    board,
                    cols,
                    diagonals,
                    antiDiagonals,
                    result
            );

            // Backtrack
            board[row][col] = '.';
            cols[col] = false;
            diagonals[diagonal] = false;
            antiDiagonals[antiDiagonal] = false;
        }
    }
}
```

**Idea:** one queen/row; prune with `cols` / diag / anti-diag sets.  
**T:** O(n!) · **S:** O(n)

### Sudoku Solver

```java
class Solution {

    private boolean[][] rows = new boolean[9][10];
    private boolean[][] cols = new boolean[9][10];
    private boolean[][] boxes = new boolean[9][10];

    public void solveSudoku(char[][] board) {

        // Build the initial state
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {

                if (board[row][col] == '.') {
                    continue;
                }

                int num = board[row][col] - '0';
                int box = (row / 3) * 3 + col / 3;

                rows[row][num] = true;
                cols[col][num] = true;
                boxes[box][num] = true;
            }
        }

        backtrack(board);
    }

    private boolean backtrack(char[][] board) {

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {

                if (board[row][col] != '.') {
                    continue;
                }

                int box = (row / 3) * 3 + col / 3;

                // Try numbers 1 through 9
                for (int num = 1; num <= 9; num++) {

                    if (rows[row][num] ||
                        cols[col][num] ||
                        boxes[box][num]) {
                        continue;
                    }

                    // Pick
                    board[row][col] = (char) ('0' + num);
                    rows[row][num] = true;
                    cols[col][num] = true;
                    boxes[box][num] = true;

                    if (backtrack(board)) {
                        return true;
                    }

                    // Backtrack
                    board[row][col] = '.';
                    rows[row][num] = false;
                    cols[col][num] = false;
                    boxes[box][num] = false;
                }

                // No number works for this cell
                return false;
            }
        }

        // No empty cells remain
        return true;
    }
}
```

**Idea:** fill first empty cell; prune with row/col/box used; backtrack on fail.  
**T:** O(9^{empty}) · **S:** O(1)

### Generate Parentheses

```java
class Solution {
    public List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();

        backtrack(n, 0, 0, new StringBuilder(), result);

        return result;
    }

    private void backtrack(
            int n,
            int open,
            int close,
            StringBuilder current,
            List<String> result) {

        if (current.length() == 2 * n) {
            result.add(current.toString());
            return;
        }

        // Add '('
        if (open < n) {
            current.append('(');

            backtrack(n, open + 1, close, current, result);

            current.deleteCharAt(current.length() - 1);
        }

        // Add ')'
        if (close < open) {
            current.append(')');

            backtrack(n, open, close + 1, current, result);

            current.deleteCharAt(current.length() - 1);
        }
    }
}
```

**Idea:** balance BT — `(` if `open < n`; `)` if `close < open`.  
**T:** O(4ⁿ/√n) · **S:** O(n)

---

## Bit → XOR

### Single Number
**Known** — XOR all. **T:** O(n) · **S:** O(1)

### Single Number II

```java
class Solution {
    public int singleNumber(int[] nums) {
        int result = 0;

        for (int bit = 0; bit < 32; bit++) {
            int count = 0;

            for (int num : nums) {
                if ((num & (1 << bit)) != 0) {
                    count++;
                }
            }

            if (count % 3 != 0) {
                result |= (1 << bit);
            }
        }

        return result;
    }
}
```

**Idea:** per-bit count mod 3; leftover bits form the unique number.  
**T:** O(n) · **S:** O(1)

### Single Number III

```java
class Solution {
    public int[] singleNumber(int[] nums) {
        int xor = 0;

        for (int num : nums) {
            xor ^= num;
        }

        // Get any bit where the two unique numbers differ.
        // xor & -xor isolates the rightmost set bit.
        int diffBit = xor & -xor;

        int a = 0;
        int b = 0;

        for (int num : nums) {
            if ((num & diffBit) == 0) {
                a ^= num;
            } else {
                b ^= num;
            }
        }

        return new int[]{a, b};
    }
}
```

**Idea:** XOR all → `a^b`; split by a differing bit; XOR each group.  
**T:** O(n) · **S:** O(1)

### Missing Number

```java
class Solution {
    public int missingNumber(int[] nums) {
        int n = nums.length;
        int xor = 0;

        // XOR all numbers that should exist: 0 ... n
        for (int i = 0; i <= n; i++) {
            xor ^= i;
        }

        // XOR all numbers that actually exist
        for (int num : nums) {
            xor ^= num;
        }

        return xor;
    }
}
```

**Idea:** XOR `0..n` with all `nums`; leftover is missing.  
**T:** O(n) · **S:** O(1)

---

## Bit → Mask / Tricks

### Number of 1 Bits

```java
class Solution {
    public int hammingWeight(int n) {
        int ans = 0;

        for (int i = 0; i < 32; i++) {
            if ((n & (1 << i)) != 0) {
                ans++;
            }
        }

        return ans;
    }
}
```

**Idea:** count set bits (also: Brian Kernighan `n &= n-1`).  
**T:** O(1) · **S:** O(1)

### Counting Bits

```java
class Solution {
    public int[] countBits(int n) {
        int[] ans = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            // i = [all bits except last] [last bit]
            // i >> 1 gives [all bits except last]
            // i & 1 gives the last bit
            ans[i] = ans[i >> 1] + (i & 1);
        }

        return ans;
    }
}
```

**Idea:** DP — `ans[i] = ans[i>>1] + (i&1)`.  
**State:** `ans[i]` = # of 1-bits in `i`.  
**T:** O(n) · **S:** O(n)

### Reverse Bits

```java
class Solution {
    public int reverseBits(int n) {
        int result = 0;

        for (int i = 0; i < 32; i++) {
            // Take the rightmost bit of n and add it to result.
            result = (result << 1) | (n & 1);

            // Remove the rightmost bit from n.
            n >>= 1;
        }

        return result;
    }
}
```

**Idea:** 32 times — shift `result` left, append `n`'s LSB, shift `n` right.  
**T:** O(1) · **S:** O(1)

### Sum of Two Integers

```java
class Solution {
    public int getSum(int a, int b) {
        while (b != 0) {
            // XOR gives sum without carry.
            int sum = a ^ b;

            // AND finds carry bits; shift left to move them.
            int carry = (a & b) << 1;

            a = sum;
            b = carry;
        }

        return a;
    }
}
```

**Idea:** XOR = sum bits; `(a&b)<<1` = carry; loop until carry 0.  
**T:** O(1) · **S:** O(1)

### Power of Two

```java
class Solution {
    public boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }
}
```

**Idea:** power of 2 has exactly one set bit → `n & (n-1) == 0`.  
**T:** O(1) · **S:** O(1)

---

## Advanced → MST

**Rule:** Explicit edge list → Kruskal · Points / complete graph → Prim

### Min Cost to Connect All Points

```java
class Solution {
    public int minCostConnectPoints(int[][] points) {
        int n = points.length;

        int[] minCost = new int[n];
        Arrays.fill(minCost, Integer.MAX_VALUE);

        boolean[] visited = new boolean[n];

        minCost[0] = 0;

        int totalCost = 0;

        for (int count = 0; count < n; count++) {

            // Pick the unvisited point with the minimum connection cost.
            int u = -1;

            for (int i = 0; i < n; i++) {
                if (!visited[i] && (u == -1 || minCost[i] < minCost[u])) {
                    u = i;
                }
            }

            visited[u] = true;
            totalCost += minCost[u];

            // Update the cheapest way to connect every unvisited point.
            for (int v = 0; v < n; v++) {
                if (!visited[v]) {
                    int distance = Math.abs(points[u][0] - points[v][0])
                                 + Math.abs(points[u][1] - points[v][1]);

                    minCost[v] = Math.min(minCost[v], distance);
                }
            }
        }

        return totalCost;
    }
}
```

**Idea:** Prim MST (dense) — grow tree by cheapest unvisited edge (Manhattan).  
**T:** O(n²) · **S:** O(n)

**Q: Why does Prim with a heap take more time than Prim with an array for this problem?**

This problem has a complete graph, meaning every point is connected to every other point.

With a heap-based Prim:
- We consider all possible edges.
- For every connection update, we insert into the heap.
- Heap insertion/removal costs O(log N).
- Since there are N² possible edges: **Time = O(N² log N)**

With array-based Prim:
- We store only the minimum cost connection for each node.
- Finding the next minimum node takes O(N).
- Updating all nodes also takes O(N).
- We do this for all N nodes: N · (N + N) = **O(N²)**

Because the graph is dense, avoiding the heap's log N overhead makes the array implementation faster.

**One-line interview answer:**  
"A heap is useful when the graph is sparse because it reduces the cost of finding the minimum edge. But this problem is a complete graph with O(N²) edges, so the heap operations add an unnecessary log N factor. Using an array-based Prim gives the optimal O(N²) time and O(N) space."

### Connecting Cities Min Cost (houses / Prim)

```java
class Solution {
    public int minCost(int[][] houses) {
        int n = houses.length;

        int[] minCost = new int[n];
        boolean[] visited = new boolean[n];

        Arrays.fill(minCost, Integer.MAX_VALUE);
        minCost[0] = 0;

        int total = 0;

        for (int count = 0; count < n; count++) {
            int u = -1;

            // Pick the cheapest unvisited house.
            for (int i = 0; i < n; i++) {
                if (!visited[i] && (u == -1 || minCost[i] < minCost[u])) {
                    u = i;
                }
            }

            visited[u] = true;
            total += minCost[u];

            // Update cheapest connection for every unvisited house.
            for (int v = 0; v < n; v++) {
                if (!visited[v]) {
                    int distance = Math.abs(houses[u][0] - houses[v][0])
                                 + Math.abs(houses[u][1] - houses[v][1]);

                    minCost[v] = Math.min(minCost[v], distance);
                }
            }
        }

        return total;
    }
}
```

**Idea:** same dense Prim as Connect Points (Manhattan MST on houses). Sparse edge-list → Kruskal.  
**T:** O(n²) · **S:** O(n)

### Critical Connections (bridges)

```java
class Solution {
    private int time = 0;

    public List<List<Integer>> criticalConnections(
            int n, List<List<Integer>> connections) {

        List<Integer>[] graph = new ArrayList[n];

        for (int i = 0; i < n; i++) {
            graph[i] = new ArrayList<>();
        }

        for (List<Integer> edge : connections) {
            int u = edge.get(0);
            int v = edge.get(1);

            graph[u].add(v);
            graph[v].add(u);
        }

        int[] disc = new int[n];
        int[] low = new int[n];

        Arrays.fill(disc, -1);

        List<List<Integer>> result = new ArrayList<>();

        dfs(0, -1, graph, disc, low, result);

        return result;
    }

    private void dfs(
            int u,
            int parent,
            List<Integer>[] graph,
            int[] disc,
            int[] low,
            List<List<Integer>> result) {

        disc[u] = low[u] = time++;

        for (int v : graph[u]) {

            // Ignore the edge we used to reach u.
            if (v == parent) {
                continue;
            }

            if (disc[v] == -1) {
                // v has not been visited yet.
                dfs(v, u, graph, disc, low, result);

                // v's subtree may have a back edge.
                low[u] = Math.min(low[u], low[v]);

                // No back edge from v's subtree can reach u or above.
                if (low[v] > disc[u]) {
                    result.add(Arrays.asList(u, v));
                }

            } else {
                // Back edge to an already visited node.
                low[u] = Math.min(low[u], disc[v]);
            }
        }
    }
}
```

**Idea:** Tarjan — bridge if `low[v] > disc[u]` (subtree can't reach u/above).  
**State:** `disc[u]` = discovery time; `low[u]` = earliest reachable disc via tree/back edges.  
**T:** O(V+E) · **S:** O(V+E)

---

## Advanced → Segment / Fenwick

### Range Sum Query Mutable (SegTree)

```java
class NumArray {
    private int[] tree;
    private int n;

    public NumArray(int[] nums) {
        n = nums.length;
        tree = new int[4 * n];

        build(0, 0, n - 1, nums);
    }

    private void build(int node, int left, int right, int[] nums) {
        if (left == right) {
            tree[node] = nums[left];
            return;
        }

        int mid = left + (right - left) / 2;

        build(2 * node + 1, left, mid, nums);
        build(2 * node + 2, mid + 1, right, nums);

        tree[node] = tree[2 * node + 1] + tree[2 * node + 2];
    }

    public void update(int index, int val) {
        update(0, 0, n - 1, index, val);
    }

    private void update(int node, int left, int right, int index, int val) {
        if (left == right) {
            tree[node] = val;
            return;
        }

        int mid = left + (right - left) / 2;

        if (index <= mid) {
            update(2 * node + 1, left, mid, index, val);
        } else {
            update(2 * node + 2, mid + 1, right, index, val);
        }

        tree[node] = tree[2 * node + 1] + tree[2 * node + 2];
    }

    public int sumRange(int left, int right) {
        return query(0, 0, n - 1, left, right);
    }

    private int query(int node, int left, int right, int ql, int qr) {
        // Completely outside the query range.
        if (right < ql || left > qr) {
            return 0;
        }

        // Completely inside the query range.
        if (ql <= left && right <= qr) {
            return tree[node];
        }

        int mid = left + (right - left) / 2;

        return query(2 * node + 1, left, mid, ql, qr)
             + query(2 * node + 2, mid + 1, right, ql, qr);
    }
}
```

**Idea:** segment tree — `tree[node]` = sum of `[left,right]`; update/query O(log n).  
**State:** `tree[node]` = range sum of segment covered by `node`.  
**T:** O(n) build · O(log n) update/query · **S:** O(n)

### Count of Smaller After Self

```java
class Solution {
    public List<Integer> countSmaller(int[] nums) {
        int OFFSET = 10_000;
        int SIZE = 20_001;

        FenwickTree bit = new FenwickTree(SIZE);
        Integer[] result = new Integer[nums.length];

        for (int i = nums.length - 1; i >= 0; i--) {
            int index = nums[i] + OFFSET + 1;

            // Count numbers strictly smaller than nums[i].
            result[i] = bit.query(index - 1);

            // Add nums[i] to the Fenwick Tree.
            bit.update(index, 1);
        }

        return Arrays.asList(result);
    }

    static class FenwickTree {
        private int[] tree;

        FenwickTree(int n) {
            tree = new int[n + 1];
        }

        void update(int index, int delta) {
            while (index < tree.length) {
                tree[index] += delta;
                index += index & -index;
            }
        }

        int query(int index) {
            int sum = 0;

            while (index > 0) {
                sum += tree[index];
                index -= index & -index;
            }

            return sum;
        }
    }
}
```

**Idea:** right→left; BIT prefix = count of smaller seen; then insert `nums[i]`.  
**State:** BIT freq of values (offset so indices ≥ 1).  
**T:** O(n log n) · **S:** O(n)

---

## Advanced → Hard DP

### Burst Balloons (interval DP)

```java
class Solution {
    public int maxCoins(int[] nums) {
        int n = nums.length;

        int[] arr = new int[n + 2];
        arr[0] = 1;
        arr[n + 1] = 1;

        for (int i = 0; i < n; i++) {
            arr[i + 1] = nums[i];
        }

        int[][] dp = new int[n + 2][n + 2];

        for (int len = 1; len <= n; len++) {
            for (int left = 1; left + len - 1 <= n; left++) {
                int right = left + len - 1;

                for (int k = left; k <= right; k++) {
                    int coins = dp[left][k - 1]
                              + dp[k + 1][right]
                              + arr[left - 1] * arr[k] * arr[right + 1];

                    dp[left][right] = Math.max(dp[left][right], coins);
                }
            }
        }

        return dp[1][n];
    }
}
```

**Idea:** interval DP — last burst `k` in `[left,right]`; pad ends with 1.  
**State:** `dp[l][r]` = max coins bursting balloons in open interval `(l,r)` / range `[l..r]`.  
**T:** O(n³) · **S:** O(n²)

### Cherry Pickup II (3D DP)

```java
class Solution {
    public int cherryPickup(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        int[][][] dp = new int[rows][cols][cols];

        for (int[][] row : dp) {
            for (int[] state : row) {
                Arrays.fill(state, -1);
            }
        }

        return dfs(0, 0, cols - 1, grid, dp);
    }

    private int dfs(
            int row,
            int col1,
            int col2,
            int[][] grid,
            int[][][] dp) {

        int rows = grid.length;
        int cols = grid[0].length;

        if (col1 < 0 || col1 >= cols || col2 < 0 || col2 >= cols) {
            return 0;
        }

        if (dp[row][col1][col2] != -1) {
            return dp[row][col1][col2];
        }

        int cherries = grid[row][col1];

        if (col1 != col2) {
            cherries += grid[row][col2];
        }

        if (row == rows - 1) {
            return dp[row][col1][col2] = cherries;
        }

        int best = 0;

        for (int move1 = -1; move1 <= 1; move1++) {
            for (int move2 = -1; move2 <= 1; move2++) {
                best = Math.max(
                    best,
                    dfs(
                        row + 1,
                        col1 + move1,
                        col2 + move2,
                        grid,
                        dp
                    )
                );
            }
        }

        return dp[row][col1][col2] = cherries + best;
    }
}
```

**Idea:** two robots down together; 9 move pairs; if same cell count once.  
**State:** `dp[row][col1][col2]` = max cherries from this row with robots at `col1`,`col2`.  
**T:** O(rows · cols²) · **S:** O(rows · cols²)

### Cherry Pickup II (bottom-up)

```java
class Solution {
    public int cherryPickup(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        int[][] dp = new int[cols][cols];

        // Base case: bottom row.
        for (int col1 = 0; col1 < cols; col1++) {
            for (int col2 = 0; col2 < cols; col2++) {
                dp[col1][col2] = grid[rows - 1][col1];

                if (col1 != col2) {
                    dp[col1][col2] += grid[rows - 1][col2];
                }
            }
        }

        // Build from bottom to top.
        for (int row = rows - 2; row >= 0; row--) {
            int[][] next = new int[cols][cols];

            for (int col1 = 0; col1 < cols; col1++) {
                for (int col2 = 0; col2 < cols; col2++) {

                    int cherries = grid[row][col1];

                    if (col1 != col2) {
                        cherries += grid[row][col2];
                    }

                    int best = 0;

                    for (int move1 = -1; move1 <= 1; move1++) {
                        for (int move2 = -1; move2 <= 1; move2++) {
                            int nextCol1 = col1 + move1;
                            int nextCol2 = col2 + move2;

                            if (nextCol1 >= 0 && nextCol1 < cols &&
                                nextCol2 >= 0 && nextCol2 < cols) {

                                best = Math.max(
                                    best,
                                    dp[nextCol1][nextCol2]
                                );
                            }
                        }
                    }

                    next[col1][col2] = cherries + best;
                }
            }

            dp = next;
        }

        return dp[0][cols - 1];
    }
}
```

**Idea:** bottom→top roll; only keep next-row `dp[col1][col2]`.  
**State:** `dp[c1][c2]` = max from current row onward with robots at `c1`,`c2`.  
**T:** O(rows · cols²) · **S:** O(cols²)

### Count of Integers (digit DP)

```java
class Solution {
    private static final int MOD = 1_000_000_007;

    private String bound;
    private int minSum;
    private int maxSum;
    private int[][][] memo;

    public int count(String num1, String num2, int min_sum, int max_sum) {
        minSum = min_sum;
        maxSum = max_sum;

        int right = countUpTo(num2);
        int left = countUpTo(decrement(num1));

        return (right - left + MOD) % MOD;
    }

    private int countUpTo(String s) {
        if (s.equals("0")) {
            return 0;
        }

        bound = s;
        int n = s.length();

        memo = new int[n][maxSum + 1][2];

        for (int i = 0; i < n; i++) {
            for (int sum = 0; sum <= maxSum; sum++) {
                Arrays.fill(memo[i][sum], -1);
            }
        }

        return dfs(0, 0, true);
    }

    private int dfs(int pos, int sum, boolean tight) {
        if (sum > maxSum) {
            return 0;
        }

        if (pos == bound.length()) {
            return sum >= minSum ? 1 : 0;
        }

        int tightIndex = tight ? 1 : 0;

        if (memo[pos][sum][tightIndex] != -1) {
            return memo[pos][sum][tightIndex];
        }

        int boundDigit = bound.charAt(pos) - '0';
        int limit = tight ? boundDigit : 9;

        long ways = 0;

        for (int digit = 0; digit <= limit; digit++) {
            boolean newTight = tight && digit == boundDigit;

            ways += dfs(pos + 1, sum + digit, newTight);
            ways %= MOD;
        }

        return memo[pos][sum][tightIndex] = (int) ways;
    }

    private String decrement(String s) {
        char[] digits = s.toCharArray();

        int i = digits.length - 1;

        while (digits[i] == '0') {
            digits[i] = '9';
            i--;
        }

        digits[i]--;

        int start = 0;

        while (start < digits.length - 1 && digits[start] == '0') {
            start++;
        }

        return new String(digits, start, digits.length - start);
    }
}
```

**Idea:** digit DP count ≤ bound with digit-sum in `[minSum,maxSum]`; answer = `f(num2)−f(num1−1)`.  
**State:** `dfs(pos,sum,tight)` = ways to finish from digit `pos` with current sum.  
**T:** O(L · maxSum · 10) · **S:** O(L · maxSum) — `L` = digit length

### Numbers At Most N Given Digit Set (digit DP)

```java
class Solution {
    private String n;
    private char[] digits;
    private long[][][] memo;

    public int atMostNGivenDigitSet(String[] digits, int n) {
        this.n = String.valueOf(n);
        this.digits = new char[digits.length];

        for (int i = 0; i < digits.length; i++) {
            this.digits[i] = digits[i].charAt(0);
        }

        memo = new long[this.n.length()][2][2];
        for (long[][] row : memo) {
            for (long[] state : row) {
                Arrays.fill(state, -1);
            }
        }

        return (int) dfs(0, true, false);
    }

    private long dfs(int pos, boolean tight, boolean started) {
        if (pos == n.length()) {
            return started ? 1 : 0;
        }

        int t = tight ? 1 : 0;
        int s = started ? 1 : 0;

        if (memo[pos][t][s] != -1) {
            return memo[pos][t][s];
        }

        int limit = tight ? n.charAt(pos) - '0' : 9;

        long count = 0;

        // Don't start the number yet.
        if (!started) {
            count += dfs(pos + 1, false, false);
        }

        // Pick one of the allowed digits.
        for (char digit : digits) {
            int value = digit - '0';

            if (value > limit) {
                break;
            }

            boolean newTight = tight && value == limit;

            count += dfs(pos + 1, newTight, true);
        }

        return memo[pos][t][s] = count;
    }
}
```

**Idea:** digit DP over allowed digits only; `started` handles shorter lengths.  
**State:** `dfs(pos,tight,started)` = count of valid numbers from this position.  
**T:** O(L · |D|) · **S:** O(L)
