package com.sahil.QuizApp.service;

import com.sahil.QuizApp.model.Question;
import com.sahil.QuizApp.model.QuestionWrapper;
import com.sahil.QuizApp.model.Quiz;
import com.sahil.QuizApp.model.Responses;
import com.sahil.QuizApp.repo.QuestionDao;
import com.sahil.QuizApp.repo.QuizDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    @Autowired
    QuizDao quizDao;

    @Autowired
    QuestionDao questionDao;


    public ResponseEntity<String> createQuiz(String category, int numsQ, String title) {

       List<Question> questionList= questionDao.findRandomQuestionByCategory(category,numsQ);

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestionList(questionList);
        quizDao.save(quiz);

        return new ResponseEntity<>("Success", HttpStatus.CREATED);

    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {

        Optional<Quiz> quiz = quizDao.findById(id);

        List<Question> questionsfromDB = quiz.get().getQuestionList();
        List<QuestionWrapper> questionForUser = new ArrayList<>();
        for(Question q : questionsfromDB){

            QuestionWrapper questionWrapper = new QuestionWrapper(q.getId(),
                    q.getQuestionTitle(),q.getOption1(),q.getOption2(),q.getOption3()
                    ,q.getOption4());

            questionForUser.add(questionWrapper);

        }

        return new ResponseEntity<>(questionForUser,HttpStatus.OK);

    }

    public ResponseEntity<Integer> calculateresult(Integer id, List<Responses> responses) {
        Quiz quiz = quizDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));;
        List<Question> questions = quiz.getQuestionList();

        int right = 0;
        int i = 0;

        for(Responses responses1 : responses){
            if(responses1.getResponse().equals(questions.get(i).getRightAnswer()))
                right++;

             i++   ;
        }

        return  new ResponseEntity<>(right, HttpStatus.OK);

    }
}
