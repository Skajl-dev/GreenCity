package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econewscomment.*;
import greencity.dto.user.UserVO;
import greencity.service.EcoNewsCommentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/econews/comments")
public class EcoNewsCommentController {
    private final EcoNewsCommentService ecoNewsCommentService;

    /**
     * Method for creating {@link EcoNewsCommentVO}.
     *
     * @param econewsId id of {@link EcoNewsVO} to add comment to.
     * @param request   - dto for {@link EcoNewsCommentVO} entity.
     * @return dto {@link greencity.dto.econewscomment.AddEcoNewsCommentDtoResponse}
     */
    @ApiOperation(value = "Add comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = AddEcoNewsCommentDtoResponse.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
    })
    @PostMapping("{econewsId}")
    public ResponseEntity<AddEcoNewsCommentDtoResponse> save(@PathVariable Long econewsId,
        @Valid @RequestBody AddEcoNewsCommentDtoRequest request,
        @ApiIgnore @CurrentUser UserVO user) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ecoNewsCommentService.save(econewsId, request, user));
    }

    /**
     * Method to get all comments to {@link EcoNewsVO} specified by ecoNewsId.
     *
     * @param ecoNewsId id of {@link EcoNewsVO}
     * @return Pageable of {@link EcoNewsCommentDto}
     */
    @ApiOperation(value = "Get all comments.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping("")
    @ApiPageable
    public ResponseEntity<PageableDto<EcoNewsCommentDto>> findAll(@ApiIgnore Pageable pageable,
        Long ecoNewsId,
        @ApiIgnore @CurrentUser UserVO user) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ecoNewsCommentService.findAllComments(pageable, user, ecoNewsId));
    }

    /**
     * Method to count not deleted comments to certain {@link EcoNewsVO}.
     *
     * @param ecoNewsId to specify {@link EcoNewsVO}
     * @return amount of comments
     */
    @ApiOperation(value = "Count comments.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping("/count/comments/{ecoNewsId}")
    public int getCountOfComments(@PathVariable Long ecoNewsId) {
        return ecoNewsCommentService.countOfComments(ecoNewsId);
    }

    /**
     * Method to get all replies to {@link EcoNewsCommentVO} specified by
     * parentCommentId.
     *
     * @param parentCommentId specifies parent comment to all replies
     * @return Pageable of {@link EcoNewsCommentDto} replies
     */
    @ApiOperation(value = "Get all replies to comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK)
    })
    @GetMapping("replies/{parentCommentId}")
    @ApiPageable
    public ResponseEntity<PageableDto<EcoNewsCommentDto>> findAllReplies(@ApiIgnore Pageable pageable,
        @PathVariable Long parentCommentId,
        @ApiIgnore @CurrentUser UserVO user) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ecoNewsCommentService.findAllReplies(pageable, parentCommentId, user));
    }

    /**
     * Method to count replies to certain {@link EcoNewsCommentVO}.
     *
     * @param parentCommentId specifies parent comment to all replies
     * @return amount of replies
     */
    @ApiOperation(value = "Get count of replies to comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping("count/replies/{parentCommentId}")
    public int getCountOfReplies(@PathVariable Long parentCommentId) {
        return ecoNewsCommentService.countReplies(parentCommentId);
    }

    /**
     * Method to mark comment as deleted.
     *
     * @param id comment id
     */
    @ApiOperation(value = "Mark comment as deleted.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @DeleteMapping("")
    public ResponseEntity<Object> delete(Long id, @ApiIgnore @CurrentUser UserVO user) {
        ecoNewsCommentService.deleteById(id, user);
        return ResponseEntity.ok().build();
    }

    /**
     * Method to update certain {@link EcoNewsCommentVO} specified by id.
     *
     * @param id   of {@link EcoNewsCommentVO} to update
     * @param text new text of {@link EcoNewsCommentVO}
     */
    @ApiOperation(value = "Update comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @PatchMapping("")
    public void update(Long id, @NotBlank String text, @ApiIgnore @CurrentUser UserVO user) {
        ecoNewsCommentService.update(text, id, user);
    }

    /**
     * Method to like/dislike certain {@link EcoNewsCommentVO} specified by id.
     *
     * @param id of {@link EcoNewsCommentVO} to like/dislike
     */
    @ApiOperation(value = "Like comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @PostMapping("like")
    public void like(Long id, @ApiIgnore @CurrentUser UserVO user) {
        ecoNewsCommentService.like(id, user);
    }

    /**
     * Method to count likes to certain {@link EcoNewsCommentVO}.
     *
     * @param id specifies comment
     * @return amount of likes
     */
    @ApiOperation(value = "Count comment likes.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping("/count/likes")
    public int getCountOfLikes(Long id) {
        return ecoNewsCommentService.countLikes(id);
    }

    /**
     * Method to like/dislike comment and count likes.
     *
     * @param id specifies comment
     * @return amount of likes
     */
    @MessageMapping("/likeAndCount")
    @SendTo("/topic/comment")
    public AmountCommentLikesDto getCountOfLike(Long id) {
        int amountLikes = ecoNewsCommentService.countLikes(id);
        return AmountCommentLikesDto.builder().amountLikes(amountLikes).id(id).build();
    }

    /**
     * Method to get all active comments to {@link EcoNewsVO} specified by
     * ecoNewsId.
     *
     * @param ecoNewsId id of {@link EcoNewsVO}
     * @return Pageable of {@link EcoNewsCommentDto}
     * @author Taras Dovganyuk
     */
    @ApiOperation(value = "Get all active comments.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping("/active")
    @ApiPageable
    public ResponseEntity<PageableDto<EcoNewsCommentDto>> getAllActiveComments(@ApiIgnore Pageable pageable,
        Long ecoNewsId,
        @ApiIgnore @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ecoNewsCommentService.getAllActiveComments(pageable, user, ecoNewsId));
    }

    /**
     * Method to get all active replies to {@link EcoNewsCommentVO} specified by
     * parentCommentId.
     *
     * @param parentCommentId specifies parent comment to all replies
     * @return Pageable of {@link EcoNewsCommentDto} replies
     * @author Dovganyuk Taras
     */
    @ApiOperation(value = "Get all active replies to comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK)
    })
    @GetMapping("replies/active/{parentCommentId}")
    @ApiPageable
    public ResponseEntity<PageableDto<EcoNewsCommentDto>> findAllActiveReplies(@ApiIgnore Pageable pageable,
        @PathVariable Long parentCommentId,
        @ApiIgnore @CurrentUser UserVO user) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ecoNewsCommentService.findAllActiveReplies(pageable, parentCommentId, user));
    }
}
