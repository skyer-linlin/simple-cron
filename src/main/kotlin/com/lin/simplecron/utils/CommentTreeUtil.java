package com.lin.simplecron.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lin.simplecron.domain.Comment;
import com.lin.simplecron.dto.CommentDto;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.*;

/**
 * com.lin.simplecron.utils
 *
 * @author quanlinlin
 * @date 2022/11/23 02:02
 * @since
 */
@UtilityClass
@Slf4j
public class CommentTreeUtil {

    public static List<CommentDto> buildSortedCommentsList(List<Comment> commentList) {
        CommentTreeNode commentTree = buildCommentTree(commentList);
        Pair<List<Integer>, Map<Integer, Integer>> pair = sortTreeNode(commentTree);
        List<Integer> sortedCmtIdList = pair.getKey();
        Map<Integer, Integer> prefixMap = pair.getValue();
        Map<Integer, Comment> commentMap = list2Map(commentList);
        List<CommentDto> sortedCmtList = Lists.newArrayList();
        for (Integer cmtId : sortedCmtIdList) {
            CommentDto commentDto = new CommentDto();
            Comment comment = commentMap.get(cmtId);
            BeanUtils.copyProperties(comment, commentDto);
            Integer prefixCnt = prefixMap.get(cmtId);
            if (prefixCnt > 0) {
                commentDto.setPrefix(StrUtil.repeat("⊙", prefixCnt) + " ");
            } else {
                commentDto.setPrefix(" ");
            }
            sortedCmtList.add(commentDto);
        }
        return Collections.unmodifiableList(sortedCmtList);
    }

    private static Map<Integer, Comment> list2Map(List<Comment> commentList) {
        Map<Integer, Comment> map = Maps.newHashMap();
        for (Comment comment : commentList) {
            map.put(comment.getId(), comment);
        }
        return map;
    }

    private static Pair<List<Integer>, Map<Integer, Integer>> sortTreeNode(CommentTreeNode commentTree) {
        List<Integer> list = Lists.newArrayList();
        Map<Integer, Integer> prefixMap = Maps.newHashMap();
        sortTreeNode(commentTree, list, prefixMap);
        return Pair.of(list, prefixMap);
    }

    private static void sortTreeNode(CommentTreeNode commentTree, List<Integer> cmtIdList, Map<Integer, Integer> prefixMap) {
        if (Objects.isNull(commentTree)) return;
        if (commentTree.getCommentId() != 0) {
            cmtIdList.add(commentTree.getCommentId());
            prefixMap.put(commentTree.getCommentId(), commentTree.getDepth() - 1);
        }
        if (commentTree.getCommentNodeTreeSet() != null) {
            for (CommentTreeNode node : commentTree.getCommentNodeTreeSet()) {
                sortTreeNode(node, cmtIdList, prefixMap);
            }
        }
    }

    private static CommentTreeNode buildCommentTree(List<Comment> commentList) {
        // 1. 先将所有评论按照时间正序排列
        // 2. 逐一取出评论
        //      如果 topCommentId 为 0,则这是第一层节点,否则是下层节点
        //      如果为下层节点,则根据 topCommentId 找到所在的子树, 将其添加进对应 set 中
        CommentTreeNode root = new CommentTreeNode();
        root.setDepth(0).setCommentId(0);
        for (Comment comment : commentList) {
            if (comment.getTopCommentId() == 0) { // 说明这是对内容的直接评论
                // 获取根节点的直接评论集合, 并添加
                TreeSet<CommentTreeNode> cmtSet = root.getCommentNodeTreeSet();
                if (cmtSet == null) {
                    root.setCommentNodeTreeSet(Sets.newTreeSet(Comparator.comparing(CommentTreeNode::getCommentId)));
                    cmtSet = root.commentNodeTreeSet;
                }
                CommentTreeNode subNode = new CommentTreeNode();
                subNode.setCommentId(comment.getId());
                subNode.setDepth(1);
                cmtSet.add(subNode);
            } else { // 走这里说明是对评论的回复, 要把回复添加到评论的子节点集合中
                TreeSet<CommentTreeNode> cmtNodeSet = root.getCommentNodeTreeSet();
                // TopCommentId属性是顶层评论的 id
                cmtNodeSet.stream().filter(node -> node.getCommentId().equals(comment.getTopCommentId()))
                    .findFirst()
                    .ifPresent(cmtNode -> {
                        // 查找这条评论具体回复的评论, 并添加到对应的子树集合中
                        CommentTreeNode replyCmtNode = searchReplyComment(cmtNode, comment);
                        if (replyCmtNode == null) {
                            log.error("有未找到父评论的回复,评论详情: {}", comment);
                            return;
                        }
                        TreeSet<CommentTreeNode> cmtSet = replyCmtNode.getCommentNodeTreeSet();
                        if (cmtSet == null) {
                            replyCmtNode.setCommentNodeTreeSet(Sets.newTreeSet(Comparator.comparing(CommentTreeNode::getCommentId)));
                            cmtSet = replyCmtNode.commentNodeTreeSet;
                        }
                        CommentTreeNode subNode = new CommentTreeNode();
                        subNode.setCommentId(comment.getId());
                        subNode.setDepth(replyCmtNode.getDepth() + 1);
                        cmtSet.add(subNode);
                    });
            }
        }
        return root;
    }

    /**
     * 查找评论具体回复回复的评论
     *
     * @param cmtNode 对于内容的直接评论, 可以确定要查找的评论就在这个子树中
     * @param comment
     * @return
     */
    private static CommentTreeNode searchReplyComment(CommentTreeNode cmtNode, Comment comment) {
        if (Objects.equals(comment.getReplyCommentId(), cmtNode.getCommentId())) {
            return cmtNode;
        }
        if (CollectionUtil.isNotEmpty(cmtNode.getCommentNodeTreeSet())) {
            TreeSet<CommentTreeNode> nodeTreeSet = cmtNode.getCommentNodeTreeSet();
            for (CommentTreeNode node : nodeTreeSet) {
                CommentTreeNode temp = searchReplyComment(node, comment);
                if (temp != null) {
                    return temp;
                }
            }
        }
        return null;
    }

    @Data
    @Accessors(chain = true)
    static class CommentTreeNode {
        Integer commentId;
        Integer depth;
        TreeSet<CommentTreeNode> commentNodeTreeSet;
    }
}
