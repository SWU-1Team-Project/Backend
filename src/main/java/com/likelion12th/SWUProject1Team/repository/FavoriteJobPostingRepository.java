package com.likelion12th.SWUProject1Team.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion12th.SWUProject1Team.entity.Member;
import com.likelion12th.SWUProject1Team.entity.FavoriteJobPosting;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteJobPostingRepository extends JpaRepository<FavoriteJobPosting, Integer> {

	// 특정 사용자의 관심 채용 공고 조회
	List<FavoriteJobPosting> findByMember(Member member);

	// 최근 2개의 관심 채용 공고를 가져오는 쿼리
	//@Query("SELECT f FROM FavoriteJobPosting f WHERE f.member.memberId = :memberId ORDER BY f.scrapDate DESC")
	//List<FavoriteJobPosting> findRecentFavorites(@Param("memberId") Integer memberId, Pageable pageable);

	@Query("SELECT f FROM FavoriteJobPosting f WHERE f.member.memberId = :memberId ORDER BY f.scrapDate DESC")
	Page<FavoriteJobPosting> findRecentFavorites(@Param("memberId") Integer memberId, Pageable pageable);

	// 전체 관심 채용 공고를 가져오는 메서드
	List<FavoriteJobPosting> findAllByMemberOrderByScrapDateDesc(Member member);
}

