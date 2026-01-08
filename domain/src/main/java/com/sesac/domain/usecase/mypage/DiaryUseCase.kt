//package com.sesac.domain.usecase.mypage
//
//import com.sesac.domain.model.Diary
//import com.sesac.domain.model.Path
//import com.sesac.domain.repository.MypageRepository
//import javax.inject.Inject
//
//
//class DiaryUseCase @Inject constructor (private val diaryRepository: MypageRepository) {
//    suspend operator fun invoke(path: Path): Diary{
//        return diaryRepository.generateDiary(path)
//    }
//}