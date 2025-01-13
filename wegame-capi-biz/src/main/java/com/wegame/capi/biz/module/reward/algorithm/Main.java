//package com.wegame.capi.biz.module.reward.algorithm;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class Main {
//
//    public static void main(String[] args) {
//        // 创建一个示例二叉树
//        TreeNode root = new TreeNode(1);
//        root.left = new TreeNode(2);
//        root.right = new TreeNode(3);
//        root.left.left = new TreeNode(5);
//        root.left.right = new TreeNode(6);
//        root.right.left = new TreeNode(7);
//        root.right.right = new TreeNode(8);
//        root.left.right.left = new TreeNode(9);
//
//        Solution solution = new Solution();
//        List<String> paths = solution.binaryTreePaths(root);
//        for (String path : paths) {
//            System.out.println(path);
//        }
//    }
//
//    // 定义二叉树的节点
//   public static class TreeNode {
//        int val;
//        TreeNode left;
//        TreeNode right;
//
//        TreeNode() {
//        }
//
//        TreeNode(int val) {
//            this.val = val;
//        }
//
//        TreeNode(int val, TreeNode left, TreeNode right) {
//            this.val = val;
//            this.left = left;
//            this.right = right;
//        }
//    }
//
//    public static class Solution {
//        public List<String> binaryTreePaths(TreeNode root) {
//            List<String> paths = new ArrayList<>();
//            if (root == null) return paths;
//            dfs(root, "", paths);
//            return paths;
//        }
//
//        private void dfs(TreeNode node, String path, List<String> paths) {
//            if (node != null) {
//                // 将当前节点的值添加到路径中
//                StringBuilder currentPath = new StringBuilder(path);
//                if (currentPath.length() > 0) {
//                    currentPath.append("->");
//                }
//                currentPath.append(node.val);
//
//                // 如果是叶子节点，将当前路径添加到结果列表中
//                if (node.left == null && node.right == null) {
//                    paths.add(currentPath.toString());
//                } else {
//                    // 递归遍历左子树和右子树
//                    dfs(node.left, currentPath.toString(), paths);
//                    dfs(node.right, currentPath.toString(), paths);
//                }
//            }
//        }
//
//
//    }
//}