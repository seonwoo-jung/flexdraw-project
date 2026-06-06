# flexdraw-project

Java Swing 기반 2D 그래픽 에디터. 도형을 그리고 편집하면서 **Command 패턴(undo/redo)**, **다형성 도형 모델**, **key-value 속성 모델**을 연습하는 학습용 데스크톱 애플리케이션이다.

## 기능

- 도형 그리기: 사각형(Rect), 타원(Ellipse), 선(Line), 자유선(Pen)
- 선택(Select) 도구로 도형 선택 및 속성 편집
- 우측 속성 패널에서 위치/크기/색상/선 두께/회전 각도 등 속성 편집
- Undo / Redo (실행 취소 / 다시 실행)
- Delete (선택 도형 삭제)
- Front / Back (z-order 앞뒤 정렬)

## 실행

```bash
javac -d out $(find src -name "*.java")
java -cp out Main
```

엔트리포인트는 `src/Main.java`이며, `GraphicsEditor` 윈도우(JFrame)를 띄운다.

## 구조

```
src/
├── Main.java                  # 엔트리포인트
├── app/
│   ├── GraphicsEditor.java    # 메인 JFrame, 패널 조립
│   ├── AppState.java          # 현재 도구/버튼 등 UI 상태
│   └── Tool.java              # 도구 enum (SELECT, RECT, ELLIPSE, LINE, PEN)
├── command/                   # Command 패턴
│   ├── Command.java           # execute() / undo() / name() 인터페이스
│   ├── AddShapeCommand.java
│   ├── RemoveShapeCommand.java
│   ├── ReorderCommand.java
│   └── UpdatePropertyCommand.java
├── model/
│   ├── core/
│   │   ├── ShapeStore.java    # 도형 목록(List) 관리, z-order
│   │   └── History.java       # undo/redo 스택 (capacity 제한)
│   └── shapes/
│       ├── AbstractShape.java # 공통 추상 도형, 속성 LinkedHashMap
│       ├── RectangleShape.java
│       ├── EllipseShape.java
│       ├── LineShape.java
│       └── FreeDrawShape.java
└── ui/
    ├── ButtonPanel.java        # 상단 도구/명령 버튼
    ├── DrawingCanvas.java      # 그리기 캔버스
    └── PropertyEditorPanel.java # 우측 속성 편집 패널
```

## 설계 포인트

- **Command 패턴**: 도형 추가/삭제/순서변경/속성변경을 각각 `Command`로 캡슐화하고 `History`의 undo/redo 스택으로 관리한다. `History`는 capacity(기본 200, 최소 10)를 두어 오래된 기록을 버린다.
- **속성 모델**: 모든 도형은 `AbstractShape`의 `LinkedHashMap<String, Object>` 속성 백(insertion order 보존)으로 위치·크기·색상·회전 등을 표현한다. 속성 편집 패널은 이 key-value 모델과 직접 연동된다.
- **도형 다형성**: `AbstractShape`를 상속한 각 도형이 자신의 렌더링과 기본 속성을 정의한다.
