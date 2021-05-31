# :rocket: 좋은 개발을 위한 Ground Rule 세팅

## 수평적인 호칭문화

* `단원` : 클로이
* `지은` : 디니
* `본혁` : 바비
* `동현` : Tcan (티캔)
* `유정` : 에리얼



# :point_up: Conventions

## git BRANCH conventions

`master` > `develop` >

feature branch 단위로 나누기

```
ex) feature/user-service_FE
```

## git COMMIT conventions

- [init]
- [merge]
- [feat]
- [modify]
- [fix]
- [refactor]
- [style]
- [docs]

```markdown
# A306 Git Commit Convention

<br>

## Branches

- **최종 merge** : `master`
- **개발 과정** : `develop`
m
- **기능 개발** : `feature/기능_BE` or `feature/기능_FE`
- **학습 문서** : `document`

<br>

## Commit Types

- **[feat]** : 새로운 기능 추가

  ex) `[feat] 댓글 달기 기능 완료`

- **[docs]** : 문서 수정/추가 (daily study log 포함)

  ex) `[docs] 백유정 210322 JPA 공부 정리`

- **[fix]** : 버그 수정

  ex) `[fix] 댓글달기 ## 에러 해결`

- **[refactor]** : 코드 리팩토링

  ex) `[refactor] BE 댓글 수정 코드 리팩토링`

- **[style]** : 코드 포맷팅, 스타일링 등

  ex) `[style] BE 댓글 작성 코드 포맷팅`

<br>

## Commit Rules

- 기능 브랜치 생성

  `git checkout -b feature/기능_BE`

  `git checkout -b feature/기능_FE`

- 코드 작성 후 해당 브랜치에 commit & push

  `git push origin feature/기능_BE`

- push 후 gitlab에서 `create merge request` 후 pr 작성

- pr 확인 후 `develop` 브랜치에 `merge`
```



## coding conventions : FE/BE