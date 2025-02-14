Project structure

```
utmctl/
├── src/main/java/
│   ├── cli/
│   │   ├── Admin.java            # 🏁 CLI 진입점 (Main class)
│   ├── config/
│   │   ├── Constants.java        # ⚙️ 전역 설정 및 상수 관리
│   │   ├── Global.java           # 🌍 글로벌 설정 클래스
│   ├── dto/
│   │   ├── TaskDto.java          # 📦 데이터 전송 객체 (DTO)
│   ├── subcommand/               # 🛠️ Picocli 기반의 subcommand
│   │   ├── Add.java              # 🏗️ `utmctl add` (slurm Job 추가)
│   │   ├── Cancel.java           # ❌ `utmctl cancel` (task 취소)
│   │   ├── Describe.java         # 🔍 `utmctl describe` (get task details)
│   │   ├── End.java              # 🛑 `utmctl end` (UTMD 종료)
│   │   ├── Get.java              # 📜 `utmctl get` (get task by username)
│   │   ├── Manual.java           # 📖 `utmctl man` (view manual page)
│   │   ├── Start.java            # ▶️ `utmctl start` (start UTMD)
│   │   ├── Version.java          # ℹ️ `utmctl version` (version 정보 출력력)
│   ├── util/
│   │   ├── PrintUtils.java       # 🎨 CLI 출력 유틸리티
│   │   ├── ProcessUtils.java     # ⚙️ 프로세스 관련 유틸
│   │   ├── TablePrinter.java     # 📊 표 형태로 출력하는 유틸리티
│   │   ├── TaskPrinter.java      # 📑 Task 출력 유틸리티(TablePrinter 상속)
├── build.gradle                  # 🏗️ Gradle 빌드 스크립트
```

utmctl <command> 의 실행 흐름

```
[add]

User Input -> Picocli (`Add.call()`) -> Admin.writeChannel() -> GTM API call (/task/add) -> http response get & PrintUtils.print(response)
```


CLI 호출 흐름

```
User Input (e.g., `utmctl add srun -c1 -t30 -l primesim:2 sleep 60)
   ↓
Admin.main() -> Picocli 처리
   ↓
Subcommand callable 실행 (Add.java)
   ↓
Admin.writeChannel() -> http request send & sync until http response
   ↓
PrintUtils.print() -> response deserialize & print response
```

https://bos-semi.atlassian.net/wiki/spaces/DI/pages/21299336/User+Taskmanager+Client 

