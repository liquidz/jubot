(ns jubot.handler
  "ハンドラーのユーティリティ"
  (:refer-clojure :exclude [comp]))

(def ^{:const true :doc "自動で収集する対象ハンドラー名の正規表現"}
  HANDLER_REGEXP #"^.*-handler$")

(defn regexp
  "正規表現と関数のリストからハンドラー関数を生成し返す

  Params
    reg-fn-list - 正規表現と関数のペア
                  正規表現にマッチした入力があった場合に、対応する関数が呼び出される
                  関数にはハンドラーへの入力に加えて正規表現にマッチした結果(re-findの戻り値)を :match として追加したものが渡される
  Return
    ハンドラー関数
  "
  [& reg-fn-list]
  {:pre [(zero? (mod (count reg-fn-list) 2))]}

  (fn [{:keys [text] :as option}]
    (reduce
      (fn [_ [r f]]
        (if (instance? java.util.regex.Pattern r)
          (some->> (re-find r text)
                   (assoc option :match)
                   f
                   reduced)
          (reduced (f option))))
      nil
      (partition 2 reg-fn-list))))

(defn comp
  "ハンドラー関数を合成する

  Params
    fs - ハンドラー関数のシーケンス
  Return
    合成されたハンドラー関数
  "
  ([] identity)
  ([& fs]
   {:pre [(every? #(or (fn? %) (var? %)) fs)]}
   (let [fs (reverse fs)]
     (fn [arg]
       (loop [ret ((first fs) arg), fs (next fs)]
         (if (and fs (nil? ret))
           (recur ((first fs) arg) (next fs))
           ret))))))

(defn public-handlers
  "指定されたネームスペース内で HANDLER_REGEXP にマッチするパブリックなハンドラー関数のシーケンスを返す

  Params
    ns-regexp - ハンドラー関数検索対象のネームスペースの正規表現
  Return
    ハンドラー関数のシーケンス
  "
  [ns-regexp]
  (->> (all-ns)
       (filter #(re-find ns-regexp (str (ns-name %))))
       (mapcat #(vals (ns-publics %)))
       (filter #(re-matches HANDLER_REGEXP (-> % meta :name str)))))

(defn collect
  "指定されたネームスペース内で HANDLER_REGEXP にマッチするパブリックなハンドラー関数を合成して返す

  Params
    ns-regexp - ハンドラー関数検索対象のネームスペースの正規表現
  Return
    ハンドラー関数
  "
  [ns-regexp]
  (if-let [handlers (seq (public-handlers ns-regexp))]
    (apply comp handlers)
    (constantly nil)))
